package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.model.UserTexture;
import com.lakeel.altla.vision.domain.repository.DocumentRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureFileRepository;
import com.lakeel.altla.vision.domain.repository.UserTextureRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

import rx.Completable;
import rx.Single;
import rx.schedulers.Schedulers;

public final class SaveUserTextureUseCase {

    @Inject
    DocumentRepository documentRepository;

    @Inject
    UserTextureRepository userTextureRepository;

    @Inject
    UserTextureFileRepository userTextureFileRepository;

    @Inject
    public SaveUserTextureUseCase() {
    }

    public Single<String> execute(String textureId, String name, String localUri,
                                  OnProgressListener onProgressListener) {
        if (name == null) throw new ArgumentNullException("name");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) throw new IllegalStateException("The user is not signed in.");

        // Generate a new texture ID.
        if (textureId == null) {
            textureId = UUID.randomUUID().toString();
        }

        UserTexture userTexture = new UserTexture();
        userTexture.userId = user.getUid();
        userTexture.textureId = textureId;
        userTexture.name = name;

        Single<Model> single = Single.just(new Model(userTexture, localUri, onProgressListener));

        if (localUri == null) {
            // Save the user texture to Firebase Database.
            return single.flatMapCompletable(this::saveUserTexture)
                         // Stream the texture ID.
                         .toSingleDefault(textureId)
                         .subscribeOn(Schedulers.io());
        } else {
            // Open the stream to the android local file.
            return single.flatMap(this::openStream)
                         // Get total bytes of the stream.
                         .flatMap(this::getTotalBytes)
                         // Upload its file to Firebase Storage.
                         .flatMap(this::uploadUserTextureFile)
                         // Save the user texture to Firebase Database.
                         .flatMapCompletable(this::saveUserTexture)
                         // Stream the texture ID.
                         .toSingleDefault(textureId)
                         .subscribeOn(Schedulers.io());
        }
    }

    private Single<Model> openStream(Model model) {
        return documentRepository.openStream(model.localUri)
                                 .map(stream -> {
                                     model.stream = stream;
                                     return model;
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

    private Single<Model> uploadUserTextureFile(Model model) {
        // Use the value obtained from the stream, because totalBytes returned by Firebase is always -1.
        return userTextureFileRepository
                .save(model.userTexture.userId,
                      model.userTexture.textureId,
                      model.stream,
                      (totalBytes, bytesTransferred) ->
                              model.onProgressListener.onProgress(model.totalBytes, bytesTransferred)
                ).toSingleDefault(model);
    }

    private Completable saveUserTexture(Model model) {
        return userTextureRepository.save(model.userTexture);
    }

    private final class Model {

        final UserTexture userTexture;

        final String localUri;

        final OnProgressListener onProgressListener;

        InputStream stream;

        long totalBytes;

        Model(UserTexture userTexture, String localUri, OnProgressListener onProgressListener) {
            this.userTexture = userTexture;
            this.localUri = localUri;
            this.onProgressListener = onProgressListener;
        }
    }
}
