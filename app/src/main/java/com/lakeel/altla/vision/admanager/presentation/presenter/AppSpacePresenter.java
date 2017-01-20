package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.AppSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceView;
import com.lakeel.altla.vision.domain.usecase.DeleteAreaDescriptionCacheUseCase;
import com.lakeel.altla.vision.domain.usecase.DeleteUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.DownloadUserAreaDescriptionFileUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheFileUseCase;
import com.lakeel.altla.vision.domain.usecase.UploadUserAreaDescriptionFileUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class AppSpacePresenter {

    @Inject
    FindAllUserAreaDescriptionsUseCase findAllUserAreaDescriptionsUseCase;

    @Inject
    GetAreaDescriptionCacheFileUseCase getAreaDescriptionCacheFileUseCase;

    @Inject
    UploadUserAreaDescriptionFileUseCase uploadUserAreaDescriptionFileUseCase;

    @Inject
    DownloadUserAreaDescriptionFileUseCase downloadUserAreaDescriptionFileUseCase;

    @Inject
    DeleteAreaDescriptionCacheUseCase deleteAreaDescriptionCacheUseCase;

    @Inject
    DeleteUserAreaDescriptionUseCase deleteUserAreaDescriptionUseCase;

    private static final Log LOG = LogFactory.getLog(AppSpacePresenter.class);

    private final List<AppSpaceItemModel> itemModels = new ArrayList<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TangoWrapper tangoWrapper;

    private AppSpaceView view;

    private long prevBytesTransferred;

    @Inject
    public AppSpacePresenter() {
    }

    public void onCreate(@NonNull TangoWrapper tangoWrapper) {
        this.tangoWrapper = tangoWrapper;
    }

    public void onCreateView(@NonNull AppSpaceView view) {
        this.view = view;
    }

    public void onStart() {
        Disposable disposable = findAllUserAreaDescriptionsUseCase
                .execute()
                .map(AppSpaceItemModelMapper::map)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemModels -> {
                    this.itemModels.clear();
                    this.itemModels.addAll(itemModels);
                    view.updateItems();
                }, e -> {
                    LOG.e("Failed to load all area description meta entries.", e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onCreateItemView(@NonNull AppSpaceItemView itemView) {
        AppSpaceItemPresenter itemPresenter = new AppSpaceItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return itemModels.size();
    }

    public void onImported() {
        view.showSnackbar(R.string.snackbar_done);
    }

    public void onDelete(int position) {
        String areaDescriptionId = itemModels.get(position).areaDescriptionId;

        Disposable disposable = deleteUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_subscription -> view.showDeleteProgressDialog())
                .doOnTerminate(() -> view.hideDeleteProgressDialog())
                .subscribe(() -> {
                    itemModels.remove(position);

                    view.updateItemRemoved(position);
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    LOG.e("Failed to delete the user area description.", e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }

    public final class AppSpaceItemPresenter {

        private AppSpaceItemView itemView;

        public void onCreateItemView(@NonNull AppSpaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            AppSpaceItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onClickImageButtonImport(int position) {
            String areaDescriptionId = itemModels.get(position).areaDescriptionId;

            Disposable disposable = getAreaDescriptionCacheFileUseCase
                    .execute(areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::showImportActivity, e -> {
                        LOG.e("Failed to import the area description into Tango.", e);
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonUpload(int position) {
            AppSpaceItemModel itemModel = itemModels.get(position);

            prevBytesTransferred = 0;

            Disposable disposable = uploadUserAreaDescriptionFileUseCase
                    .execute(itemModel.areaDescriptionId, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        view.setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(_subscription -> view.showUploadProgressDialog())
                    .doOnTerminate(() -> view.hideUploadProgressDialog())
                    .subscribe(() -> {
                        itemModel.fileUploaded = true;

                        view.updateItem(position);
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        LOG.e("Failed to upload the user area description.", e);
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonDownload(int position) {
            AppSpaceItemModel itemModel = itemModels.get(position);

            prevBytesTransferred = 0;

            Disposable disposable = downloadUserAreaDescriptionFileUseCase
                    .execute(itemModel.areaDescriptionId, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        view.setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(_subscription -> view.showUploadProgressDialog())
                    .doOnTerminate(() -> view.hideUploadProgressDialog())
                    .subscribe(() -> {
                        itemModel.fileCached = true;

                        view.updateItem(position);
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        LOG.e("Failed to download the user area description.", e);
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonSynced(int position) {
            AppSpaceItemModel itemModel = itemModels.get(position);

            Disposable disposable = deleteAreaDescriptionCacheUseCase
                    .execute(itemModel.areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        itemModel.fileCached = false;

                        view.updateItem(position);
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        LOG.e("Failed to delete the area description cache.", e);
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonEdit(int position) {
            String areaDescriptionId = itemModels.get(position).areaDescriptionId;

            view.showEditUserAreaDescriptionFragment(areaDescriptionId);
        }

        public void onClickImageButtonDelete(int position) {
            view.showDeleteConfirmationDialog(position);
        }
    }
}
