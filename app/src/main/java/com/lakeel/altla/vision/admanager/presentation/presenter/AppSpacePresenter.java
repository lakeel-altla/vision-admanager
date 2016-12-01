package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.DeleteMetaDataUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.FindAllMetaDatasUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.GetContentPathUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.UploadContentUseCase;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.AppSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceView;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class AppSpacePresenter {

    @Inject
    FindAllMetaDatasUseCase findAllMetaDatasUseCase;

    @Inject
    GetContentPathUseCase getContentPathUseCase;

    @Inject
    DeleteMetaDataUseCase deleteMetaDataUseCase;

    @Inject
    UploadContentUseCase uploadContentUseCase;

    private static final Log LOG = LogFactory.getLog(AppSpacePresenter.class);

    private final List<AppSpaceItemModel> itemModels = new ArrayList<>();

    private final AppSpaceItemModelMapper mapper = new AppSpaceItemModelMapper();

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private AppSpaceView view;

    private long prevBytesTransferred;

    @Inject
    public AppSpacePresenter() {
    }

    public void onCreateView(@NonNull AppSpaceView view) {
        this.view = view;
    }

    public void onStart() {
        Subscription subscription = findAllMetaDatasUseCase
                .execute()
                .map(mapper::map)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemModels -> {
                    this.itemModels.clear();
                    this.itemModels.addAll(itemModels);
                    view.updateItems();
                }, e -> {
                    LOG.e("Loading area description meta datas failed.", e);
                    view.showSnackbar(R.string.snackbar_load_failed);
                });
        compositeSubscription.add(subscription);
    }

    public void onStop() {
        compositeSubscription.clear();
        view.hideUploadProgressDialog();
    }

    public void onCreateItemView(@NonNull AppSpaceItemView itemView) {
        AppSpaceItemPresenter itemPresenter = new AppSpaceItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return itemModels.size();
    }

    public void onDelete(@IntRange(from = 0) int position) {
        String uuid = itemModels.get(position).uuid;

        Subscription subscription = deleteMetaDataUseCase
                .execute(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    itemModels.remove(position);
                    view.updateItemRemoved(position);
                    view.showSnackbar(R.string.snackbar_deleted);
                }, e -> {
                    LOG.e("Deleting app space meta data failed.", e);
                    view.showSnackbar(R.string.snackbar_delete_failed);
                });
        compositeSubscription.add(subscription);
    }

    public final class AppSpaceItemPresenter {

        private AppSpaceItemView itemView;

        public void onCreateItemView(@NonNull AppSpaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(@IntRange(from = 0) int position) {
            AppSpaceItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onImport(@IntRange(from = 0) int position) {
            String uuid = itemModels.get(position).uuid;

            Subscription subscription = getContentPathUseCase
                    .execute(uuid)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::showImportActivity, e -> {
                        LOG.e("Importing area description failed.", e);
                        view.showSnackbar(R.string.snackbar_import_failed);
                    });
            compositeSubscription.add(subscription);
        }

        public void onUpload(@IntRange(from = 0) int position) {
            String uuid = itemModels.get(position).uuid;

            view.showUploadProgressDialog();

            Subscription subscription = uploadContentUseCase
                    .execute(uuid, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - prevBytesTransferred;
                        prevBytesTransferred = bytesTransferred;
                        view.setUploadProgressDialogProgress(totalBytes, increment);
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        view.hideUploadProgressDialog();
                        view.showSnackbar(R.string.snackbar_uploaded);
                    }, e -> {
                        LOG.e("Uploading area description failed.", e);
                        view.hideUploadProgressDialog();
                        view.showSnackbar(R.string.snackbar_upload_failed);
                    });
            compositeSubscription.add(subscription);
        }
    }
}
