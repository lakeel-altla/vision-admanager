package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.TangoSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceView;
import com.lakeel.altla.vision.domain.usecase.DeleteTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllTangoAreaDescriptionsUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheDirectoryUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class TangoSpacePresenter {

    private static final Log LOG = LogFactory.getLog(TangoSpacePresenter.class);

    @Inject
    FindAllTangoAreaDescriptionsUseCase findAllTangoAreaDescriptionsUseCase;

    @Inject
    GetAreaDescriptionCacheDirectoryUseCase getAreaDescriptionCacheDirectoryUseCase;

    @Inject
    DeleteTangoAreaDescriptionUseCase deleteTangoAreaDescriptionUseCase;

    private final List<TangoSpaceItemModel> itemModels = new ArrayList<>();

    private final TangoSpaceItemModelMapper mapper = new TangoSpaceItemModelMapper();

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private TangoWrapper tangoWrapper;

    private TangoSpaceView view;

    private String exportingId;

    private long prevBytesTransferred;

    @Inject
    public TangoSpacePresenter() {
    }

    public void onCreate(@NonNull TangoWrapper tangoWrapper) {
        this.tangoWrapper = tangoWrapper;
    }

    public void onCreateView(@NonNull TangoSpaceView view) {
        this.view = view;
    }

    public void onStart() {
        Subscription subscription = findAllTangoAreaDescriptionsUseCase
                .execute(tangoWrapper.getTango())
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
        if (exportingId == null) {
            throw new IllegalStateException("exportingUuid == null");
        }

        prevBytesTransferred = 0;

        // TODO
//        view.showUploadProgressDialog();
//
//        Subscription subscription = addAreaDescriptionUseCase
//                .execute(exportingId, (totalBytes, bytesTransferred) -> {
//                    long increment = bytesTransferred - prevBytesTransferred;
//                    prevBytesTransferred = bytesTransferred;
//                    view.setUploadProgressDialogProgress(totalBytes, increment);
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(entry -> {
//                               view.hideUploadProgressDialog();
//                               view.showSnackbar(R.string.snackbar_done);
//                           }, e -> {
//                               LOG.e(String.format("Failed to add the area description: id = %s", exportingId), e);
//                               view.hideUploadProgressDialog();
//                               view.showSnackbar(R.string.snackbar_failed);
//                           }
//
//                );
//        compositeSubscription.add(subscription);
    }

    public final class TangoSpaceItemPresenter {

        private TangoSpaceItemView itemView;

        public void onCreateItemView(@NonNull TangoSpaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            TangoSpaceItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onClickButtonExport(int position) {
            Subscription subscription = getAreaDescriptionCacheDirectoryUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        exportingId = itemModels.get(position).id;
                        view.showExportActivity(exportingId, directory);
                    });
            compositeSubscription.add(subscription);
        }

        public void onClickButtonDelete() {
            itemView.showDeleteAreaDescriptionConfirmationDialog();
        }

        public void onDelete(int position) {
            String areaDescriptionId = itemModels.get(position).id;

            Subscription subscription = deleteTangoAreaDescriptionUseCase
                    .execute(tangoWrapper.getTango(), areaDescriptionId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        itemModels.remove(position);
                        view.updateItemRemoved(position);
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        LOG.e("Deleting area description failed.", e);
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeSubscription.add(subscription);
        }
    }
}
