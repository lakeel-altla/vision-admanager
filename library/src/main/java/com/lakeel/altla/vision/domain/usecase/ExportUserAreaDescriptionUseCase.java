package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import javax.inject.Inject;

import rx.Single;
import rx.schedulers.Schedulers;

public final class ExportUserAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;

    @Inject
    public ExportUserAreaDescriptionUseCase() {
    }

    public Single<UserAreaDescription> execute(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        // Convert arguments to the internal model.
        return Single.just(new Model(tango, user.getUid(), areaDescriptionId))
                     // Get the metadata from Tango.
                     .flatMap(this::getMetadataFromTango)
                     // Save the user area description to Firebase Database.
                     .flatMap(this::saveUserAreaDescription)
                     // Return the user area description.
                     .map(model -> model.userAreaDescription)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> getMetadataFromTango(Model model) {
        return tangoAreaDescriptionMetadataRepository
                .find(model.tango, model.areaDescriptionId)
                .map(metaData -> {
                    UserAreaDescription userAreaDescription = new UserAreaDescription();
                    userAreaDescription.userId = model.userId;
                    userAreaDescription.areaDescriptionId = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
                    userAreaDescription.name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
                    userAreaDescription.creationTime = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);

                    model.userAreaDescription = userAreaDescription;

                    return model;
                })
                .toSingle();
    }

    private Single<Model> saveUserAreaDescription(Model model) {
        return userAreaDescriptionRepository
                .save(model.userAreaDescription)
                .toSingleDefault(model);
    }

    private final class Model {

        final Tango tango;

        final String userId;

        final String areaDescriptionId;

        UserAreaDescription userAreaDescription;

        Model(Tango tango, String userId, String areaDescriptionId) {
            this.tango = tango;
            this.userId = userId;
            this.areaDescriptionId = areaDescriptionId;
        }
    }
}
