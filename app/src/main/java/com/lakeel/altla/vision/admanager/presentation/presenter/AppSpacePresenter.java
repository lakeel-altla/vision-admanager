package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.AppSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceView;
import com.lakeel.altla.vision.domain.usecase.DeleteUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllUserAreaDescriptionsUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class AppSpacePresenter {

    @Inject
    FindAllUserAreaDescriptionsUseCase findAllUserAreaDescriptionsUseCase;

    @Inject
    DeleteUserAreaDescriptionUseCase deleteUserAreaDescriptionUseCase;

    private static final Log LOG = LogFactory.getLog(AppSpacePresenter.class);

    private final List<AppSpaceItemModel> itemModels = new ArrayList<>();

    private final AppSpaceItemModelMapper mapper = new AppSpaceItemModelMapper();

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private TangoWrapper tangoWrapper;

    private AppSpaceView view;

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
        Subscription subscription = findAllUserAreaDescriptionsUseCase
                .execute(tangoWrapper.getTango())
                .map(mapper::map)
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
        compositeSubscription.add(subscription);
    }

    public void onStop() {
        compositeSubscription.clear();
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

    public final class AppSpaceItemPresenter {

        private AppSpaceItemView itemView;

        public void onCreateItemView(@NonNull AppSpaceItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            AppSpaceItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onClickButtonDelete() {
            itemView.showDeleteAreaDescriptionConfirmationDialog();
        }

        public void onClickButtonImport(int position) {
            // TODO
//            String id = itemModels.get(position).id;
//
//            Subscription subscription = getAreaDescriptionCacheUseCase
//                    .execute(id)
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(view::showImportActivity, e -> {
//                        LOG.e("Failed to import the area description into Tango.", e);
//                        view.showSnackbar(R.string.snackbar_failed);
//                    });
//            compositeSubscription.add(subscription);
        }

        public void onDelete(int position) {
            String areaDescriptionId = itemModels.get(position).id;
            view.showDeleteProgressDialog();

            Subscription subscription = deleteUserAreaDescriptionUseCase
                    .execute(areaDescriptionId)
                    .doOnSubscribe(_subscription -> view.showDeleteProgressDialog())
                    .doOnUnsubscribe(() -> view.hideDeleteProgressDialog())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        itemModels.remove(position);

                        view.updateItemRemoved(position);
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        LOG.e("Failed to delete the app area description.", e);
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeSubscription.add(subscription);
        }
    }
}
