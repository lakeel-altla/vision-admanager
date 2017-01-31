package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.TangoAreaDescriptionItemModelMapper;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.TangoAreaDescriptionItemModel;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionItemView;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionListView;
import com.lakeel.altla.vision.domain.usecase.DeleteTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.ExportUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindAllTangoAreaDescriptionsUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheDirectoryUseCase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class TangoAreaDescriptionListPresenter implements TangoWrapper.OnTangoReadyListener {

    private static final Log LOG = LogFactory.getLog(TangoAreaDescriptionListPresenter.class);

    @Inject
    FindAllTangoAreaDescriptionsUseCase findAllTangoAreaDescriptionsUseCase;

    @Inject
    GetAreaDescriptionCacheDirectoryUseCase getAreaDescriptionCacheDirectoryUseCase;

    @Inject
    ExportUserAreaDescriptionUseCase exportUserAreaDescriptionUseCase;

    @Inject
    DeleteTangoAreaDescriptionUseCase deleteTangoAreaDescriptionUseCase;

    @Inject
    TangoWrapper tangoWrapper;

    private final List<TangoAreaDescriptionItemModel> itemModels = new ArrayList<>();

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private TangoAreaDescriptionListView view;

    private String exportingAreaDescriptionId;

    @Inject
    public TangoAreaDescriptionListPresenter() {
    }

    @Override
    public void onTangoReady(Tango tango) {
        Disposable disposable = findAllTangoAreaDescriptionsUseCase
                .execute(tangoWrapper.getTango())
                .map(TangoAreaDescriptionItemModelMapper::map)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(itemModels -> {
                    this.itemModels.clear();
                    this.itemModels.addAll(itemModels);
                    view.updateItems();
                }, e -> {
                    LOG.e("Loading area description meta datas failed.", e);
                });
        compositeDisposable.add(disposable);
    }

    public void onCreateView(@NonNull TangoAreaDescriptionListView view) {
        this.view = view;
    }

    public void onStop() {
        compositeDisposable.clear();
    }

    public void onResume() {
        tangoWrapper.addOnTangoReadyListener(this);
    }

    public void onPause() {
        tangoWrapper.removeOnTangoReadyListener(this);
    }

    public void onExported() {
        if (exportingAreaDescriptionId == null) {
            throw new IllegalStateException("'exportingAreaDescriptionId' is null.");
        }

        Disposable disposable = exportUserAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), exportingAreaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userAreaDescription -> {
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    LOG.e(String.format("Failed to export the tango area description: areaDescriptionId = %s",
                                        exportingAreaDescriptionId), e);
                    view.showSnackbar(R.string.snackbar_failed);
                });
        compositeDisposable.add(disposable);

        view.showSnackbar(R.string.snackbar_done);
    }

    public void onDelete(int position) {
        String areaDescriptionId = itemModels.get(position).areaDescriptionId;

        Disposable disposable = deleteTangoAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    itemModels.remove(position);
                    view.updateItemRemoved(position);
                    view.showSnackbar(R.string.snackbar_done);
                }, e -> {
                    LOG.e(String.format("Failed to delete the tango area description: areaDescriptionId = %s",
                                        areaDescriptionId), e);
                    view.showSnackbar(R.string.snackbar_failed);
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

        private TangoAreaDescriptionItemView itemView;

        public void onCreateItemView(@NonNull TangoAreaDescriptionItemView itemView) {
            this.itemView = itemView;
        }

        public void onBind(int position) {
            TangoAreaDescriptionItemModel itemModel = itemModels.get(position);
            itemView.showModel(itemModel);
        }

        public void onClickImageButtonExport(int position) {
            Disposable disposable = getAreaDescriptionCacheDirectoryUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        exportingAreaDescriptionId = itemModels.get(position).areaDescriptionId;
                        view.showExportActivity(exportingAreaDescriptionId, directory);
                    });
            compositeDisposable.add(disposable);
        }

        public void onClickImageButtonDelete(int position) {
            view.showDeleteConfirmationDialog(position);
        }
    }
}
