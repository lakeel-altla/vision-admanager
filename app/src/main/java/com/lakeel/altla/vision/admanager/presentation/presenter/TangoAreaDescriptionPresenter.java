package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.mapper.TangoAreaDescriptionModelMapper;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionView;
import com.lakeel.altla.vision.domain.usecase.DeleteTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.ExportUserAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.FindTangoAreaDescriptionUseCase;
import com.lakeel.altla.vision.domain.usecase.GetAreaDescriptionCacheDirectoryUseCase;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public final class TangoAreaDescriptionPresenter extends BasePresenter<TangoAreaDescriptionView>
        implements TangoWrapper.OnTangoReadyListener {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    FindTangoAreaDescriptionUseCase findTangoAreaDescriptionUseCase;

    @Inject
    GetAreaDescriptionCacheDirectoryUseCase getAreaDescriptionCacheDirectoryUseCase;

    @Inject
    ExportUserAreaDescriptionUseCase exportUserAreaDescriptionUseCase;

    @Inject
    DeleteTangoAreaDescriptionUseCase deleteTangoAreaDescriptionUseCase;

    @Inject
    TangoWrapper tangoWrapper;

    private String areaDescriptionId;

    @Inject
    public TangoAreaDescriptionPresenter() {
    }

    @NonNull
    public static Bundle createArguments(@NonNull String areaDescriptionId) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_DESCRIPTION_ID, areaDescriptionId);
        return bundle;
    }

    @Override
    public void onCreate(@Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onCreate(arguments, savedInstanceState);

        if (arguments == null) throw new ArgumentNullException("arguments");

        String areaDescriptionId = arguments.getString(ARG_AREA_DESCRIPTION_ID, null);
        if (areaDescriptionId == null) {
            throw new IllegalStateException(String.format("Argument '%s' must be not null.", ARG_AREA_DESCRIPTION_ID));
        }

        this.areaDescriptionId = areaDescriptionId;
    }

    @Override
    public void onTangoReady(Tango tango) {
        Disposable disposable = findTangoAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), areaDescriptionId)
                .map(TangoAreaDescriptionModelMapper::map)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(model -> {
                    getView().onModelUpdated(model);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
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

    public void onActionDelete() {
        getView().onShowDeleteConfirmationDialog();
    }

    public void onActionExport() {
        Disposable disposable = getAreaDescriptionCacheDirectoryUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(directory -> {
                    getView().onShowTangoAreaDescriptionExportActivity(areaDescriptionId, directory);
                });
        manageDisposable(disposable);
    }

    public void onExported() {
        Disposable disposable = exportUserAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(userAreaDescription -> {
                    getView().onSnackbar(R.string.snackbar_done);
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);

        getView().onSnackbar(R.string.snackbar_done);
    }

    public void onDelete() {
        Disposable disposable = deleteTangoAreaDescriptionUseCase
                .execute(tangoWrapper.getTango(), areaDescriptionId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    getView().onDeleted();
                }, e -> {
                    getLog().e(String.format("Failed: areaDescriptionId = %s", areaDescriptionId), e);
                    getView().onSnackbar(R.string.snackbar_failed);
                });
        manageDisposable(disposable);
    }
}
