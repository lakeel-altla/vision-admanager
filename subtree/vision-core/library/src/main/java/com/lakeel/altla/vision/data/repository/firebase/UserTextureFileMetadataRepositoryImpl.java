package com.lakeel.altla.vision.data.repository.firebase;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import com.lakeel.altla.rx.tasks.RxGmsTask;
import com.lakeel.altla.vision.ArgumentNullException;
import com.lakeel.altla.vision.domain.model.TextureFileMetadata;
import com.lakeel.altla.vision.domain.repository.UserTextureFileMetadataRepository;

import rx.Observable;
import rx.Single;

public final class UserTextureFileMetadataRepositoryImpl implements UserTextureFileMetadataRepository {

    private static final String PATH_USER_TEXTURES = "userTextures";

    private final StorageReference rootReference;

    public UserTextureFileMetadataRepositoryImpl(StorageReference rootReference) {
        if (rootReference == null) throw new ArgumentNullException("rootReference");

        this.rootReference = rootReference;
    }

    @Override
    public Observable<TextureFileMetadata> find(String userId, String textureId) {
        if (userId == null) throw new ArgumentNullException("userId");
        if (textureId == null) throw new ArgumentNullException("textureId");

        return Single.<Task<StorageMetadata>>create(subscriber -> {
            Task<StorageMetadata> task = rootReference.child(PATH_USER_TEXTURES)
                                                      .child(userId)
                                                      .child(textureId)
                                                      .getMetadata();
            subscriber.onSuccess(task);
        }).flatMapObservable(RxGmsTask::asObservable)
          .map(storageMetadata -> {
              TextureFileMetadata fileMetadata = new TextureFileMetadata();
              fileMetadata.createTimeMillis = storageMetadata.getCreationTimeMillis();
              fileMetadata.updateTimeMillis = storageMetadata.getUpdatedTimeMillis();
              return fileMetadata;
          });
    }
}
