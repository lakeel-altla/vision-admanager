package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

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
import io.reactivex.disposables.Disposable;

public final class TangoAreaDescriptionListPresenter extends BasePresenter<TangoAreaDescriptionListView>
        implements TangoWrapper.OnTangoReadyListener {

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
                    getView().updateItems();
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        tangoWrapper.addOnTangoReadyListener(this);
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

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
                    getView().showSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", exportingAreaDescriptionId), e);
                    getView().showSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);

        getView().showSnackbar(R.string.snackbar_done);
    }

    public void onDelete(int position) {
        String areaDescriptionId = itemModels.get(position).areaDescriptionId;

        Disposable disposable = deleteTangoAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    itemModels.remove(position);
                    getView().updateItemRemoved(position);
                    getView().showSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().showSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
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
                        getView().showExportActivity(exportingAreaDescriptionId, directory);
                    });
            manageDisposable(disposable);
        }

        public void onClickImageButtonDelete(int position) {
            getView().showDeleteConfirmationDialog(position);
        }
    }
}
