package com.lakeel.altla.vision.admanager.domain.usecase.appspace;

import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.domain.repository.AppContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetadataRepository;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseContentRepository;
import com.lakeel.altla.vision.admanager.domain.repository.FirebaseMetadataRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

public final class UploadContentUseCase {

    @Inject
    AppMetadataRepository appMetadataRepository;

    @Inject
    AppContentRepository appContentRepository;

    @Inject
    FirebaseMetadataRepository firebaseMetadataRepository;

    @Inject
    FirebaseContentRepository firebaseContentRepository;

    private Action1<? super FileInputStream> closeFileInputStream = stream -> {
        try {
            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    @Inject
    public UploadContentUseCase() {
    }

    public Single<String> execute(String uuid, OnProgressListener onProgressListener) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return appMetadataRepository.find(uuid)
                                    .flatMap(this::saveMetaData)
                                    .flatMap(metaData -> getContentFile(uuid))
                                    .flatMap(file -> saveContent(uuid, file, onProgressListener))
                                    .toSingle()
                                    .subscribeOn(Schedulers.io());
    }

    private Observable<AreaDescriptionMetadata> saveMetaData(AreaDescriptionMetadata metadata) {
        return firebaseMetadataRepository.save(metadata)
                                         .toObservable()
                                         .subscribeOn(Schedulers.io());
    }

    private Observable<File> getContentFile(String uuid) {
        return appContentRepository.getFilePath(uuid)
                                   .map(File::new)
                                   .toObservable();
    }

    private Observable<String> saveContent(String uuid, File file, OnProgressListener onProgressListener) {
        // Use using() to close the stream after asynchronous upload processing.
        return Observable.using(createFileInputStream(file),
                                stream -> saveContent(uuid, stream, onProgressListener),
                                closeFileInputStream).subscribeOn(Schedulers.io());
    }

    private Func0<FileInputStream> createFileInputStream(File file) {
        return () -> {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        };
    }


    private Observable<String> saveContent(String uuid, FileInputStream stream, OnProgressListener onProgressListener) {
        try {
            // Firebase が返す totalBytes が常に -1 なので、ストリームから得られる available 値を用いる。
            long available = stream.available();
            return firebaseContentRepository.save(
                    uuid, stream,
                    (totalBytes, bytesTransferred) -> onProgressListener.onProgress(available, bytesTransferred)
            ).toObservable();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public interface OnProgressListener {

        void onProgress(long totalBytes, long bytesTransferred);
    }
}
