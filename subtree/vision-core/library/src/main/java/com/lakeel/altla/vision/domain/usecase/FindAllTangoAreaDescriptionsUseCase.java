package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.tango.TangoAreaDescriptionMetaDataHelper;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public final class FindAllTangoAreaDescriptionsUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    public FindAllTangoAreaDescriptionsUseCase() {
    }

    public Observable<TangoAreaDescription> execute(Tango tango) {
        if (tango == null) throw new ArgumentNullException("tango");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        return Observable.<TangoAreaDescription>create(subscriber -> {
            List<TangoAreaDescriptionMetaData> metaDatas = tangoAreaDescriptionMetadataRepository.findAll(tango);
            for (TangoAreaDescriptionMetaData metaData : metaDatas) {
                TangoAreaDescription tangoAreaDescription = map(metaData);
                subscriber.onNext(tangoAreaDescription);
            }
            subscriber.onCompleted();
        }).subscribeOn(Schedulers.io());
    }

    private static final TangoAreaDescription map(TangoAreaDescriptionMetaData metaData) {
        String areaDescriptionId = TangoAreaDescriptionMetaDataHelper.getUuid(metaData);
        String name = TangoAreaDescriptionMetaDataHelper.getName(metaData);
        long creationTime = TangoAreaDescriptionMetaDataHelper.getMsSinceEpoch(metaData);

        return new TangoAreaDescription(areaDescriptionId, name, creationTime);
    }
}
