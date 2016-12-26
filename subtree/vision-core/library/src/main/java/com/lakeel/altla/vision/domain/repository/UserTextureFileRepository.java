package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import java.io.File;
import java.io.InputStream;

import rx.Completable;

public interface UserTextureFileRepository {

    Completable save(String userId, String textureId, InputStream stream, OnProgressListener onProgressListener);

    Completable delete(String userId, String textureId);

    Completable download(String userId, String textureId, File destination, OnProgressListener onProgressListener);
}
