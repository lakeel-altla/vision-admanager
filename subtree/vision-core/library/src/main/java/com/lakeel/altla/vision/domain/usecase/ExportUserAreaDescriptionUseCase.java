package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionIdRepository;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public final class ExportUserAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionIdRepository tangoAreaDescriptionIdRepository;

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    public ExportUserAreaDescriptionUseCase() {
    }

    @NonNull
    public Single<UserAreaDescription> execute(@NonNull Tango tango, @NonNull String areaDescriptionId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Single.<UserAreaDescription>create(e -> {
            List<String> areaDescriptionIds = tangoAreaDescriptionIdRepository.findAll(tango);
            if (areaDescriptionIds.contains(areaDescriptionId)) {
                TangoAreaDescriptionMetaData metaData = tangoAreaDescriptionMetadataRepository.get(
                        tango, areaDescriptionId);
                UserAreaDescription userAreaDescription = new UserAreaDescription();
                userAreaDescription.userId = user.getUid();
                userAreaDescription.areaDescriptionId = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
                userAreaDescription.name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
                userAreaDescription.createdAt = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);

                // Save the user area description to Firebase Database.
                userAreaDescriptionRepository.save(userAreaDescription);

                e.onSuccess(userAreaDescription);
            } else {
                e.onError(new IllegalStateException(
                        "Tango metadata not be found: areaDescriptionId = " + areaDescriptionId));
            }
        }).subscribeOn(Schedulers.io());
    }
}
