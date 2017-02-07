package com.lakeel.altla.vision.domain.usecase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.vision.data.repository.android.DocumentRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureFileRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserTextureRepository;
import com.lakeel.altla.vision.domain.helper.OnProgressListener;
import com.lakeel.altla.vision.domain.model.UserTexture;

import android.support.annotation.NonNull;

import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

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

    @NonNull
    public Single<String> execute(String textureId, @NonNull String name, String localUri,
                                  OnProgressListener onProgressListener) {
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
        return Single.create(e -> {
            model.stream = documentRepository.openStream(model.localUri);
            e.onSuccess(model);
        });
    }

    private Single<Model> getTotalBytes(Model model) {
        return Single.<Long>create(e -> {
            long totalBytes = model.stream.available();
            e.onSuccess(totalBytes);
        }).map(totalBytes -> {
            model.totalBytes = totalBytes;
            return model;
        });
    }

    private Single<Model> uploadUserTextureFile(Model model) {
        return Single.create(e -> userTextureFileRepository.save(
                model.userTexture.userId,
                model.userTexture.textureId,
                model.stream,
                aVoid -> e.onSuccess(model),
                e::onError,
                (totalBytes, bytesTransferred) ->
                        model.onProgressListener.onProgress(model.totalBytes, bytesTransferred)
        ));
    }

    private Completable saveUserTexture(Model model) {
        return Completable.create(e -> {
            userTextureRepository.save(model.userTexture);
            e.onComplete();
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
