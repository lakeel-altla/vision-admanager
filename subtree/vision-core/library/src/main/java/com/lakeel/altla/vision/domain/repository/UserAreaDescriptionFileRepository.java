package com.lakeel.altla.vision.domain.repository;

import com.lakeel.altla.vision.domain.helper.OnProgressListener;

import java.io.File;
import java.io.InputStream;

import rx.Completable;
import rx.Single;

public interface UserAreaDescriptionFileRepository {

    Single<Boolean> exists(String userId, String areaDescriptionId);

    Completable upload(String userId, String areaDescriptionId, InputStream stream,
                       OnProgressListener onProgressListener);

    Completable download(String userId, String areaDescriptionId, File destination,
                         OnProgressListener onProgressListener);

    Completable delete(String userId, String areaDescriptionId);
}
