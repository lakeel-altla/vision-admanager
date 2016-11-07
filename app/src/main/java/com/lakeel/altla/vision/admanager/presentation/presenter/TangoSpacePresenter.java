package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.GetContentDirectoryUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.SaveMetaDataUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.tangospace.DeleteContentUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.tangospace.FindAllMetaDatasUseCase;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.TangoSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceView;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class TangoSpacePresenter {

    @Inject
    FindAllMetaDatasUseCase mFindAllMetaDatasUseCase;

    @Inject
    GetContentDirectoryUseCase mGetContentDirectoryUseCase;

    @Inject
    SaveMetaDataUseCase mSaveMetaDataUseCase;

    @Inject
    DeleteContentUseCase mDeleteContentUseCase;

    private static final Log LOGGER = LogFactory.getLog(TangoSpacePresenter.class);

    private final List<TangoSpaceItemModel> mItemModels = new ArrayList<>();

    private final TangoSpaceItemModelMapper mMapper = new TangoSpaceItemModelMapper();

    private final CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    private TangoSpaceView mView;

    private String mExportingUuid;

    @Inject
    public TangoSpacePresenter() {
    }

    public void onCreateView(@NonNull TangoSpaceView view) {
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
                    LOGGER.e("Loading area description meta datas failed.", e);
                });
        mCompositeSubscription.add(subscription);
    }

    public void onStop() {
        mCompositeSubscription.clear();
    }

    public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
        TangoSpaceItemPresenter itemPresenter = new TangoSpaceItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return mItemModels.size();
    }

    public void exportMetaData() {
        if (mExportingUuid == null) {
            throw new IllegalStateException("mExportingUuid == null");
        }

        TangoSpaceView view = mView;

        Subscription subscription = mSaveMetaDataUseCase
                .execute(mExportingUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> mView.showSnackbar(R.string.snackbar_exported), e -> {
                    LOGGER.e("Exporting area description meta data failed.", e);
                    view.showSnackbar(R.string.snackbar_export_failed);
                });
        mCompositeSubscription.add(subscription);
    }

    public void onDelete(@IntRange(from = 0) int position) {
        String uuid = mItemModels.get(position).uuid;

        Subscription subscription = mDeleteContentUseCase
                .execute(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    mItemModels.remove(position);
                    mView.updateItemRemoved(position);
                    mView.showSnackbar(R.string.snackbar_deleted);
                }, e -> {
                    LOGGER.e("Deleting area description failed.", e);
                    mView.showSnackbar(R.string.snackbar_delete_failed);
                });
        mCompositeSubscription.add(subscription);
    }

    public final class TangoSpaceItemPresenter {

        private TangoSpaceItemView mItemView;

        public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
            mItemView = itemView;
        }

        public void onBind(@IntRange(from = 0) int position) {
            TangoSpaceItemModel itemModel = mItemModels.get(position);
            mItemView.showModel(itemModel);
        }

        public void onExport(@IntRange(from = 0) int position) {
            Subscription subscription = mGetContentDirectoryUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        mExportingUuid = mItemModels.get(position).uuid;
                        mView.showExportActivity(mExportingUuid, directory);
                    });
            mCompositeSubscription.add(subscription);
        }
    }
}
