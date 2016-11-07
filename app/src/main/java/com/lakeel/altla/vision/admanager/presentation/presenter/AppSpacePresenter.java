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
    FindAllMetaDatasUseCase mFindAllMetaDatasUseCase;

    @Inject
    GetContentPathUseCase mGetContentPathUseCase;

    @Inject
    DeleteMetaDataUseCase mDeleteMetaDataUseCase;

    @Inject
    UploadContentUseCase mUploadContentUseCase;

    private static final Log LOG = LogFactory.getLog(AppSpacePresenter.class);

    private final List<AppSpaceItemModel> mItemModels = new ArrayList<>();

    private final AppSpaceItemModelMapper mMapper = new AppSpaceItemModelMapper();

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private AppSpaceView mView;

    private long mPrevBytesTransferred;

    @Inject
    public AppSpacePresenter() {
    }

    public void onCreateView(@NonNull AppSpaceView view) {
        mView = view;
    }

    public void onStart() {
        Subscription subscription = mFindAllMetaDatasUseCase
                .execute()
                .map(mMapper::map)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemModels -> {
                    mItemModels.clear();
                    mItemModels.addAll(itemModels);
                    mView.updateItems();
                }, e -> {
                    LOG.e("Loading area description meta datas failed.", e);
                    mView.showSnackbar(R.string.snackbar_load_failed);
                });
        mCompositeSubscription.add(subscription);
    }

    public void onStop() {
        mCompositeSubscription.clear();
        mView.hideUploadProgressDialog();
    }

    public void onCreateItemView(@NonNull AppSpaceItemView itemView) {
        AppSpaceItemPresenter itemPresenter = new AppSpaceItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return mItemModels.size();
    }

    public void onDelete(@IntRange(from = 0) int position) {
        String uuid = mItemModels.get(position).uuid;

        Subscription subscription = mDeleteMetaDataUseCase
                .execute(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mItemModels.remove(position);
                    mView.updateItemRemoved(position);
                    mView.showSnackbar(R.string.snackbar_deleted);
                }, e -> {
                    LOG.e("Deleting app space meta data failed.", e);
                    mView.showSnackbar(R.string.snackbar_delete_failed);
                });
        mCompositeSubscription.add(subscription);
    }

    public final class AppSpaceItemPresenter {

        private AppSpaceItemView mItemView;

        public void onCreateItemView(@NonNull AppSpaceItemView itemView) {
            mItemView = itemView;
        }

        public void onBind(@IntRange(from = 0) int position) {
            AppSpaceItemModel itemModel = mItemModels.get(position);
            mItemView.showModel(itemModel);
        }

        public void onImport(@IntRange(from = 0) int position) {
            AppSpaceView view = mView;

            String uuid = mItemModels.get(position).uuid;

            Subscription subscription = mGetContentPathUseCase
                    .execute(uuid)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::showImportActivity, e -> {
                        LOG.e("Importing area description failed.", e);
                        view.showSnackbar(R.string.snackbar_import_failed);
                    });
            mCompositeSubscription.add(subscription);
        }

        public void onUpload(@IntRange(from = 0) int position) {
            AppSpaceView view = mView;

            String uuid = mItemModels.get(position).uuid;

            view.showUploadProgressDialog();

            Subscription subscription = mUploadContentUseCase
                    .execute(uuid, (totalBytes, bytesTransferred) -> {
                        long increment = bytesTransferred - mPrevBytesTransferred;
                        mPrevBytesTransferred = bytesTransferred;
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
            mCompositeSubscription.add(subscription);
        }
    }
}
