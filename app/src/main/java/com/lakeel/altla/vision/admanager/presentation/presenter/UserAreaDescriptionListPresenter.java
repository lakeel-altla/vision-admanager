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
import io.reactivex.disposables.CompositeDisposable;
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

    private final List<UserAreaDescriptionItemModel> itemModels = new ArrayList<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private long prevBytesTransferred;

    @Inject
    public UserAreaDescriptionListPresenter() {
    }

    @Override
    public void onStart() {
        super.onStart();

        Disposable disposable = findAllUserAreaDescriptionsUseCase
                .execute()
                .map(UserAreaDescriptionItemModelMapper::map)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemModels -> {
                    this.itemModels.clear();
                    this.itemModels.addAll(itemModels);
                    getView().updateItems();
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onStop() {
        super.onStop();

        compositeDisposable.clear();
    }

    public void onImported() {
        getView().showSnackbar(R.string.snackbar_done);
    }

    public void onDelete(int position) {
        String areaDescriptionId = itemModels.get(position).areaDescriptionId;

        Disposable disposable = deleteUserAreaDescriptionUseCase
                .execute(areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(_subscription -> getView().showDeleteProgressDialog())
                .doOnTerminate(() -> getView().hideDeleteProgressDialog())
                .subscribe(() -> {
                    itemModels.remove(position);

                    getView().updateItemRemoved(position);
                    getView().showSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e("Failed.", e);
                    getView().showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);
    }

    public int getItemCount() {
        return itemModels.size();
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
            UserAreaDescriptionItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onClickImageButtonImport(int position) {
            String areaDescriptionId = itemModels.get(position).areaDescriptionId;

            Disposable disposable = getAreaDescriptionCacheFileUseCase
                    .execute(areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getView()::showImportActivity, e -> {
                        getLog().e("Failed.", e);
                        getView().showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonUpload(int position) {
            UserAreaDescriptionItemModel itemModel = itemModels.get(position);

            prevBytesTransferred = 0;

            Disposable disposable = uploadUserAreaDescriptionFileUseCase
                    .execute(itemModel.areaDescriptionId, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        getView().setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(_subscription -> getView().showUploadProgressDialog())
                    .doOnTerminate(() -> getView().hideUploadProgressDialog())
                    .subscribe(() -> {
                        itemModel.fileUploaded = true;

                        getView().updateItem(position);
                        getView().showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonDownload(int position) {
            UserAreaDescriptionItemModel itemModel = itemModels.get(position);

            prevBytesTransferred = 0;

            Disposable disposable = downloadUserAreaDescriptionFileUseCase
                    .execute(itemModel.areaDescriptionId, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        getView().setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(_subscription -> getView().showUploadProgressDialog())
                    .doOnTerminate(() -> getView().hideUploadProgressDialog())
                    .subscribe(() -> {
                        itemModel.fileCached = true;

                        getView().updateItem(position);
                        getView().showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonSynced(int position) {
            UserAreaDescriptionItemModel itemModel = itemModels.get(position);

            Disposable disposable = deleteAreaDescriptionCacheUseCase
                    .execute(itemModel.areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        itemModel.fileCached = false;

                        getView().updateItem(position);
                        getView().showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().showSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonEdit(int position) {
            String areaDescriptionId = itemModels.get(position).areaDescriptionId;

            getView().showEditUserAreaDescriptionFragment(areaDescriptionId);
        }

        public void onClickImageButtonDelete(int position) {
            getView().showDeleteConfirmationDialog(position);
        }
    }
}
