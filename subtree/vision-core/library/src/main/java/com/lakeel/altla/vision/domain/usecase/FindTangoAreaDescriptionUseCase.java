package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoAreaDescriptionMetaData;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionIdRepository;
import com.lakeel.altla.vision.data.repository.android.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.domain.mapper.TangoAreaDescriptionMapper;
import com.lakeel.altla.vision.domain.model.TangoAreaDescription;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Maybe;
import io.reactivex.schedulers.Schedulers;

public final class FindTangoAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionIdRepository tangoAreaDescriptionIdRepository;

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    public FindTangoAreaDescriptionUseCase() {
    }

    public Maybe<TangoAreaDescription> execute(Tango tango, String areaDescriptionId) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return Maybe.<TangoAreaDescription>create(e -> {
            List<String> areaDescriptionIds = tangoAreaDescriptionIdRepository.findAll(tango);
            if (areaDescriptionIds.contains(areaDescriptionId)) {
                TangoAreaDescriptionMetaData metaData = tangoAreaDescriptionMetadataRepository.get(
                        tango, areaDescriptionId);
                TangoAreaDescription tangoAreaDescription = TangoAreaDescriptionMapper.map(metaData);
                e.onSuccess(tangoAreaDescription);
            } else {
                e.onComplete();
            }
        }).subscribeOn(Schedulers.io());
    }
}
