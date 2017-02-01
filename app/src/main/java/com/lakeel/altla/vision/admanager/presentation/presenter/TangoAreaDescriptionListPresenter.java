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

    private final List<TangoAreaDescriptionItemModel> items = new ArrayList<>();

    private String exportingAreaDescriptionId;

    @Inject
    public TangoAreaDescriptionListPresenter() {
    }

    @Override
    public void onTangoReady(Tango tango) {
        Disposable disposable = findAllTangoAreaDescriptionsUseCase
                .execute(tangoWrapper.getTango())
                .map(TangoAreaDescriptionItemModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    items.add(model);
                    getView().onItemInserted(items.size() - 1);
                }, e -> {
                    getLog().e("Failed.", e);
                });
        manageDisposable(disposable);
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        items.clear();
        getView().onItemsUpdated();

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
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", exportingAreaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);

        getView().onSnackbar(R.string.snackbar_done);
    }

    public void onDelete(int position) {
        String areaDescriptionId = items.get(position).areaDescriptionId;

        Disposable disposable = deleteTangoAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    items.remove(position);
                    getView().onItemRemoved(position);
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }

    public int getItemCount() {
        return items.size();
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
            TangoAreaDescriptionItemModel itemModel = items.get(position);
            itemView.onModelUpdated(itemModel);
        }

        public void onClickImageButtonExport(int position) {
            Disposable disposable = getAreaDescriptionCacheDirectoryUseCase
                    .execute()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(directory -> {
                        exportingAreaDescriptionId = items.get(position).areaDescriptionId;
                        getView().onExportActivity(exportingAreaDescriptionId, directory);
                    });
            manageDisposable(disposable);
        }

        public void onClickImageButtonDelete(int position) {
            getView().onShowDeleteConfirmationDialog(position);
        }
    }
}
