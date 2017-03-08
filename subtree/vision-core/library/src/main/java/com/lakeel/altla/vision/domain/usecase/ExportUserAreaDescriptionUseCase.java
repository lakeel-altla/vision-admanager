package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionIdRepository;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserAreaDescriptionRepository;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.model.AreaDescription;

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
    CurrentUserResolver currentUserResolver;

    @Inject
    public ExportUserAreaDescriptionUseCase() {
    }

    @NonNull
    public Single<AreaDescription> execute(@NonNull Tango tango, @NonNull String areaDescriptionId) {
        String userId = currentUserResolver.getUserId();

        return Single.<AreaDescription>create(e -> {
            List<String> areaDescriptionIds = tangoAreaDescriptionIdRepository.findAll(tango);
            if (areaDescriptionIds.contains(areaDescriptionId)) {
                TangoAreaDescriptionMetaData metaData = tangoAreaDescriptionMetadataRepository.get(
                        tango, areaDescriptionId);
                AreaDescription areaDescription = new AreaDescription();
                areaDescription.setId(TangoAreaDescriptionMetaDataHelper.getUuid(metaData));
                areaDescription.setUserId(userId);
                areaDescription.setName(TangoAreaDescriptionMetaDataHelper.getName(metaData));

                // Save the user area description to Firebase Database.
                userAreaDescriptionRepository.save(areaDescription);

                e.onSuccess(areaDescription);
            } else {
                e.onError(new IllegalStateException(
                        "Tango metadata not be found: areaDescriptionId = " + areaDescriptionId));
            }
        }).subscribeOn(Schedulers.io());
    }
}
