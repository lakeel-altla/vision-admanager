package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.model.UserTexture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

import rx.Completable;
import rx.CompletableSubscriber;
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
        return Single.create(subscriber -> {
            try {
                model.stream = documentRepository.openStream(model.localUri);
                subscriber.onSuccess(model);
            } catch (FileNotFoundException e) {
                subscriber.onError(e);
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

    private Single<Model> uploadUserTextureFile(Model model) {
        return Single.create(subscriber -> {
            userTextureFileRepository.save(
                    model.userTexture.userId,
                    model.userTexture.textureId,
                    model.stream,
                    aVoid -> subscriber.onSuccess(model),
                    subscriber::onError,
                    (totalBytes, bytesTransferred) ->
                            model.onProgressListener.onProgress(model.totalBytes, bytesTransferred)
            );
        });
    }

    private Completable saveUserTexture(Model model) {
        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                userTextureRepository.save(model.userTexture);
                subscriber.onCompleted();
            }
        });
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
