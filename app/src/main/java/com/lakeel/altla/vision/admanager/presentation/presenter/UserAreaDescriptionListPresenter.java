package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.UserAreaDescriptionItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaDescriptionItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaDescriptionListView;
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
import io.reactivex.disposables.Disposable;

public final class UserAreaDescriptionListPresenter extends BasePresenter<UserAreaDescriptionListView> {

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

    private final List<UserAreaDescriptionItemModel> items = new ArrayList<>();

    private long prevBytesTransferred;

    @Inject
    public UserAreaDescriptionListPresenter() {
    }

    @Override
    protected void onStartOverride() {
        super.onStartOverride();

        items.clear();
        getView().onItemsUpdated();

        Disposable disposable = findAllUserAreaDescriptionsUseCase
                .execute()
                .map(UserAreaDescriptionItemModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public void onImported() {
        getView().onSnackbar(R.string.snackbar_done);
    }

    public void onDelete(int position) {
        String areaDescriptionId = items.get(position).areaDescriptionId;

        Disposable disposable = deleteUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_subscription -> getView().onShowDeleteProgressDialog())
                .doOnTerminate(() -> getView().onHideDeleteProgressDialog())
                .subscribe(() -> {
                    items.remove(position);

                    getView().onItemRemoved(position);
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public int getItemCount() {
        return items.size();
    }

    public ItemPresenter createItemPresenter() {
        return new ItemPresenter();
    }

    public final class ItemPresenter {

        private UserAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull UserAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            UserAreaDescriptionItemModel itemModel = items.get(position);
            itemView.onModelUpdated(itemModel);
        }

        public void onClickImageButtonImport(int position) {
            String areaDescriptionId = items.get(position).areaDescriptionId;

            Disposable disposable = getAreaDescriptionCacheFileUseCase
                    .execute(areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getView()::onShowImportActivity, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        }

        public void onClickImageButtonUpload(int position) {
            UserAreaDescriptionItemModel itemModel = items.get(position);

            prevBytesTransferred = 0;

            Disposable disposable = uploadUserAreaDescriptionFileUseCase
                    .execute(itemModel.areaDescriptionId, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        getView().onUploadProgressUpdated(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(_subscription -> getView().onShowUploadProgressDialog())
                    .doOnTerminate(() -> getView().onHideUploadProgressDialog())
                    .subscribe(() -> {
                        itemModel.fileUploaded = true;

                        getView().onItemInserted(position);
                        getView().onSnackbar(R.string.snackbar_done);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        }

        public void onClickImageButtonDownload(int position) {
            UserAreaDescriptionItemModel itemModel = items.get(position);

            prevBytesTransferred = 0;

            Disposable disposable = downloadUserAreaDescriptionFileUseCase
                    .execute(itemModel.areaDescriptionId, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        getView().onUploadProgressUpdated(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(_subscription -> getView().onShowUploadProgressDialog())
                    .doOnTerminate(() -> getView().onHideUploadProgressDialog())
                    .subscribe(() -> {
                        itemModel.fileCached = true;

                        getView().onItemInserted(position);
                        getView().onSnackbar(R.string.snackbar_done);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        }

        public void onClickImageButtonSynced(int position) {
            UserAreaDescriptionItemModel itemModel = items.get(position);

            Disposable disposable = deleteAreaDescriptionCacheUseCase
                    .execute(itemModel.areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        itemModel.fileCached = false;

                        getView().onItemInserted(position);
                        getView().onSnackbar(R.string.snackbar_done);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            manageDisposable(disposable);
        }

        public void onClickImageButtonEdit(int position) {
            String areaDescriptionId = items.get(position).areaDescriptionId;

            getView().onShowEditUserAreaDescriptionView(areaDescriptionId);
        }

        public void onClickImageButtonDelete(int position) {
            getView().onShowDeleteConfirmationDialog(position);
        }
    }
}
