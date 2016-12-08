package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.domain.usecase.DeleteAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllAreaDescriptionEntriesUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheUseCase;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.AppSpaceItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.AppSpaceItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceItemView;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceView;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public final class AppSpacePresenter {

    @Inject
    FindAllAreaDescriptionEntriesUseCase findAllAreaDescriptionEntriesUseCase;

    @Inject
    DeleteAreaDescriptionUseCase deleteAreaDescriptionUseCase;

    @Inject
    GetAreaDescriptionCacheUseCase getAreaDescriptionCacheUseCase;

    private static final Log LOG = LogFactory.getLog(AppSpacePresenter.class);

    private final List<AppSpaceItemModel> itemModels = new ArrayList<>();

    private final AppSpaceItemModelMapper mapper = new AppSpaceItemModelMapper();

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    private AppSpaceView view;

    @Inject
    public AppSpacePresenter() {
    }

    public void onCreateView(@NonNull AppSpaceView view) {
        this.view = view;
    }

    public void onStart() {
        Subscription subscription = findAllAreaDescriptionEntriesUseCase
                .execute()
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
            String id = itemModels.get(position).id;

            Subscription subscription = getAreaDescriptionCacheUseCase
                    .execute(id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(view::showImportActivity, e -> {
                        LOG.e("Failed to import the area description into Tango.", e);
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeSubscription.add(subscription);
        }

        public void onDelete(int position) {
            String id = itemModels.get(position).id;

            LOG.d("Deleting the app area description: id = %s", id);

            view.showDeleteProgressDialog();

            Subscription subscription = deleteAreaDescriptionUseCase
                    .execute(id)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s -> {
                        LOG.d("Deleted the app area description.");
                        itemModels.remove(position);

                        view.hideDeleteProgressDialog();
                        view.updateItemRemoved(position);
                        view.showSnackbar(R.string.snackbar_done);
                    }, e -> {
                        LOG.e("Failed to delete the app area description.", e);

                        view.hideDeleteProgressDialog();
                        view.showSnackbar(R.string.snackbar_failed);
                    });
            compositeSubscription.add(subscription);
        }
    }
}
