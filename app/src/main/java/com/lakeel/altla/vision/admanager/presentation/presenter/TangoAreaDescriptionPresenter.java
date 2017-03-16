package com.lakeel.altla.vision.admanager.presentation.presenter;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.view.TangoAreaDescriptionView;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.model.TangoAreaDescription;
import com.lakeel.altla.vision.presentation.presenter.BasePresenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class TangoAreaDescriptionPresenter extends BasePresenter<TangoAreaDescriptionView>
        implements TangoWrapper.OnTangoReadyListener {

    private static final String ARG_AREA_DESCRIPTION_ID = "areaDescriptionId";

    @Inject
    VisionService visionService;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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
    protected void onCreateViewOverride() {
        super.onCreateViewOverride();

        getView().onUpdateTitle(null);
    }

    @Override
    public void onTangoReady(Tango tango) {
        runOnUiThread(() -> {
            Disposable disposable = Maybe
                    .<TangoAreaDescription>create(e -> {
                        TangoAreaDescription tangoAreaDescription =
                                visionService.getTangoAreaDescriptionApi()
                                             .findTangoAreaDescriptionById(areaDescriptionId);
                        if (tangoAreaDescription == null) {
                            e.onComplete();
                        } else {
                            e.onSuccess(tangoAreaDescription);
                        }
                    })
                    .map(Model::new)
                    .flatMap(model -> {
                        return Maybe.<Model>create(e -> {
                            visionService.getUserAreaDescriptionApi()
                                         .findAreaDescriptionById(areaDescriptionId, areaDescription -> {
                                             model.exported = (areaDescription != null);
                                             e.onSuccess(model);
                                         }, e::onError);
                        });
                    })
                    .subscribe(model -> {
                        getView().onUpdateTitle(model.tangoAreaDescription.getName());
                        getView().onUpdateActionExport(!model.exported);
                        getView().onUpdateAreaDescriptionId(model.tangoAreaDescription.getAreaDescriptionId());
                        getView().onUpdateExported(model.exported);
                        getView().onUpdateName(model.tangoAreaDescription.getName());
                        getView().onUpdateCreatedAt(model.tangoAreaDescription.getCreatedAt());
                    }, e -> {
                        getLog().e("Failed.", e);
                        getView().onSnackbar(R.string.snackbar_failed);
                    }, () -> {
                        getLog().e("Entity not found.");
                        getView().onSnackbar(R.string.snackbar_failed);
                    });
            compositeDisposable.add(disposable);
        });
    }

    @Override
    protected void onResumeOverride() {
        super.onResumeOverride();

        visionService.getTangoWrapper().addOnTangoReadyListener(this);
    }

    @Override
    protected void onPauseOverride() {
        super.onPauseOverride();

        visionService.getTangoWrapper().removeOnTangoReadyListener(this);
    }

    @Override
    protected void onStopOverride() {
        super.onStopOverride();

        compositeDisposable.clear();
    }

    public void onActionDelete() {
        getView().onShowDeleteConfirmationDialog();
    }

    public void onActionExport() {
        File directory = visionService.getUserAreaDescriptionApi().getAreaDescriptionCacheDirectory();
        getView().onShowTangoAreaDescriptionExportActivity(areaDescriptionId, directory);
    }

    public void onExported() {
        visionService.getTangoAreaDescriptionApi().exportTangoAreaDescriptionById(areaDescriptionId);
        getView().onUpdateActionExport(false);
        getView().onUpdateExported(true);
        getView().onSnackbar(R.string.snackbar_done);
    }

    public void onDelete() {
        visionService.getTangoAreaDescriptionApi().deleteTangoAreaDescriptionById(areaDescriptionId);
        getView().onSnackbar(R.string.snackbar_done);
        getView().onBackView();
    }

    private final class Model {

        final TangoAreaDescription tangoAreaDescription;

        boolean exported;

        Model(@NonNull TangoAreaDescription tangoAreaDescription) {
            this.tangoAreaDescription = tangoAreaDescription;
        }
    }
}
