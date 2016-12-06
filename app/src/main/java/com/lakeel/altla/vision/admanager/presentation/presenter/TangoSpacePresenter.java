package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.GetContentDirectoryUseCase;
import com.lakeel.altla.vision.admanager.domain.usecase.appspace.SaveMetadataUseCase;
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
    FindAllMetaDatasUseCase findAllMetaDatasUseCase;

    @Inject
    GetContentDirectoryUseCase getContentDirectoryUseCase;

    @Inject
    SaveMetadataUseCase saveMetadataUseCase;

    @Inject
    DeleteContentUseCase deleteContentUseCase;

    private static final Log LOG = LogFactory.getLog(TangoSpacePresenter.class);

    private final List<TangoSpaceItemModel> itemModels = new ArrayList<>();

    private final TangoSpaceItemModelMapper mapper = new TangoSpaceItemModelMapper();

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private TangoSpaceView view;

    private String exportingUuid;

    @Inject
    public TangoSpacePresenter() {
    }

    public void onCreateView(@NonNull TangoSpaceView view) {
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
                });
        compositeSubscription.add(subscription);
    }

    public void onStop() {
        compositeSubscription.clear();
    }

    public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
        TangoSpaceItemPresenter itemPresenter = new TangoSpaceItemPresenter();
        itemPresenter.onCreateItemView(itemView);
        itemView.setItemPresenter(itemPresenter);
    }

    public int getItemCount() {
        return itemModels.size();
    }

    public void onExported() {
        if (exportingUuid == null) {
            throw new IllegalStateException("exportingUuid == null");
        }

        Subscription subscription = saveMetadataUseCase
                .execute(exportingUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> view.showSnackbar(R.string.snackbar_done), e -> {
                    LOG.e("Exporting area description meta data failed.", e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);
    }

    public void onDelete(@IntRange(from = 0) int position) {
        String uuid = itemModels.get(position).uuid;

        Subscription subscription = deleteContentUseCase
                .execute(uuid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    itemModels.remove(position);
                    view.updateItemRemoved(position);
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    LOG.e("Deleting area description failed.", e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeSubscription.add(subscription);
    }

    public final class TangoSpaceItemPresenter {

        private TangoSpaceItemView itemView;

        public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(@IntRange(from = 0) int position) {
            TangoSpaceItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onExport(@IntRange(from = 0) int position) {
            Subscription subscription = getContentDirectoryUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        exportingUuid = itemModels.get(position).uuid;
                        view.showExportActivity(exportingUuid, directory);
                    });
            compositeSubscription.add(subscription);
        }
    }
}
