package com.lakeel.altla.vision.domain.usecase;

import com.google.atap.tangoservice.Tango;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.mapper.UserAreaDescriptionMapper;
import com.lakeel.altla.vision.domain.model.UserAreaDescription;
import com.lakeel.altla.vision.domain.repository.AreaDescriptionCacheRepository;
import com.lakeel.altla.vision.domain.repository.TangoAreaDescriptionMetadataRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionFileRepository;
import com.lakeel.altla.vision.domain.repository.UserAreaDescriptionRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import rx.Completable;
import rx.Single;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public final class SaveUserAreaDescriptionUseCase {

    @Inject
    TangoAreaDescriptionMetadataRepository tangoAreaDescriptionMetadataRepository;

    @Inject
    UserAreaDescriptionRepository userAreaDescriptionRepository;

    @Inject
    UserAreaDescriptionFileRepository userAreaDescriptionFileRepository;

    @Inject
    AreaDescriptionCacheRepository areaDescriptionCacheRepository;


    private final Action1<? super InputStream> closeStream = stream -> {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    @Inject
    public SaveUserAreaDescriptionUseCase() {
    }

    public Single<UserAreaDescription> execute(Tango tango, String areaDescriptionId,
                                               OnProgressListener onProgressListener) {
        if (tango == null) throw new ArgumentNullException("tango");
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        // Convert arguments to the internal model.
        return Single.just(new Model(tango, user.getUid(), areaDescriptionId, onProgressListener))
                     // Get the metadata from Tango.
                     .flatMap(this::getMetadataFromTango)
                     // Open the stream of the area description file as cache.
                     .flatMap(this::createCacheStream)
                     // Get the total bytes of it.
                     .flatMap(this::getTotalBytes)
                     // Upload it to Firebase Storage.
                     .flatMap(this::uploadUserAreaDescriptionFile)
                     // Save the user area description to Firebase Database.
                     .flatMap(this::saveUserAreaDescription)
                     // Return the added entry.
                     .map(model -> model.userAreaDescription)
                     .subscribeOn(Schedulers.io());
    }

    private Single<Model> getMetadataFromTango(Model model) {
        return tangoAreaDescriptionMetadataRepository
                .find(model.tango, model.areaDescriptionId)
                .map(metaData -> {
                    model.userAreaDescription = UserAreaDescriptionMapper.map(model.userId, metaData);
                    return model;
                })
                .toSingle();
    }

    private Single<Model> createCacheStream(Model model) {
        return areaDescriptionCacheRepository
                .getFile(model.areaDescriptionId)
                .map(path -> {
                    try {
                        model.stream = new FileInputStream(path);
                        return model;
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private Single<Model> getTotalBytes(Model model) {
        return Single.<Long>create(subscriber -> {
            try {
                long totalBytes = model.stream.available();
                subscriber.onSuccess(totalBytes);
            } catch (IOException e) {
                subscriber.onError(e);
            }
        }).map(totalBytes -> {
            model.totalBytes = totalBytes;
            return model;
        });
    }

    private Single<Model> uploadUserAreaDescriptionFile(Model model) {
        return Completable
                .using(() -> model.stream,
                       stream -> userAreaDescriptionFileRepository.upload(
                               model.userId, model.areaDescriptionId, model.stream,
                               (totalBytes, bytesTransferred) ->
                                       model.onProgressListener.onProgress(model.totalBytes, bytesTransferred)),
                       closeStream)
                .toSingleDefault(model);
    }

    private Single<Model> saveUserAreaDescription(Model model) {
        return userAreaDescriptionRepository
                .save(model.userAreaDescription)
                .toSingleDefault(model)
                .map(_model -> {
                    // Mark as synced.
                    _model.userAreaDescription.synced = true;
                    return _model;
                });
    }

    private final class Model {

        final Tango tango;

        final String userId;

        final String areaDescriptionId;

        final OnProgressListener onProgressListener;

        UserAreaDescription userAreaDescription;

        InputStream stream;

        long totalBytes;

        Model(Tango tango, String userId, String areaDescriptionId, OnProgressListener onProgressListener) {
            this.tango = tango;
            this.userId = userId;
            this.areaDescriptionId = areaDescriptionId;
            this.onProgressListener = onProgressListener;
        }
    }
}
