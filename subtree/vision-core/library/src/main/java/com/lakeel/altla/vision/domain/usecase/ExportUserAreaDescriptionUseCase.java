package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import javax.inject.Inject;

import rx.Single;

public final class ExportUserAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public ExportUserAreaDescriptionUseCase() {
    }

    public Single<UserAreaDescription> execute(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Single.create(subscriber -> {
            // Get the metadata from Tango.
            TangoAreaDescriptionMetaData metaData = tangoAreaDescriptionMetadataRepository.find(
                    tango, areaDescriptionId);
            if (metaData != null) {
                UserAreaDescription userAreaDescription = new UserAreaDescription();
                userAreaDescription.userId = user.getUid();
                userAreaDescription.areaDescriptionId = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
                userAreaDescription.name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
                userAreaDescription.creationTime = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);

                // Save the user area description to Firebase Database.
                userAreaDescriptionRepository.save(userAreaDescription);

                subscriber.onSuccess(userAreaDescription);
            } else {
                subscriber.onError(new IllegalStateException(
                        "Tango metadata not be found: areaDescriptionId = " + areaDescriptionId));
            }
        });
    }
}
