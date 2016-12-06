package com.lakeel.altla.vision.admanager.data.repository.android;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.ArgumentNullException;
import com.lakeel.altla.vision.admanager.data.repository.mapper.MetadataMapper;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetadata;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetadataRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.inject.Inject;

import rx.Observable;
import rx.Single;

public final class AppMetadataRepositoryImpl implements AppMetadataRepository {

    private static final Log LOG = LogFactory.getLog(AppMetadataRepositoryImpl.class);

    private static final String EXTENTION = ".json";

    private final MetadataMapper mapper = new MetadataMapper();

    private final File baseDirectory;

    @Inject
    public AppMetadataRepositoryImpl(File baseDirectory) {
        if (baseDirectory == null) throw new ArgumentNullException("baseDirectory");

        this.baseDirectory = baseDirectory;
        if (!this.baseDirectory.exists()) {
            if (!this.baseDirectory.mkdirs()) {
                throw new IllegalStateException("Creating a directory failed: " + this.baseDirectory);
            }
        }
    }

    @Override
    public Observable<AreaDescriptionMetadata> find(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return getMetaDataJsonFile(uuid)
                .flatMap(this::readMetaDataJson)
                .map(mapper::fromJson);
    }

    @Override
    public Single<AreaDescriptionMetadata> save(AreaDescriptionMetadata metadata) {
        if (metadata == null) throw new ArgumentNullException("metadata");

        return createMetaDataJsonFile(metadata.uuid)
                .flatMap(file -> writeMetaDataJson(file, metadata))
                .toSingle();
    }

    @Override
    public Single<String> delete(String uuid) {
        if (uuid == null) throw new ArgumentNullException("uuid");

        return getMetaDataJsonFile(uuid)
                .flatMap(this::deleteFile)
                .map(file -> uuid)
                .toSingle();
    }

    private Observable<File> getMetaDataJsonFile(String uuid) {
        return createMetaDataJsonFile(uuid)
                .filter(File::exists);
    }

    private Observable<File> createMetaDataJsonFile(String uuid) {
        String fileName = uuid + EXTENTION;
        File file = new File(baseDirectory, fileName);
        return Observable.just(file);
    }

    private Observable<String> readMetaDataJson(File file) {
        return Observable.<String>create(subscriber -> {
            LOG.d("Reading json meta data from '%s'", file.getPath());
            try {
                String json;
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    try (StringWriter writer = new StringWriter()) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            writer.write(line);
                        }
                        json = writer.toString();
                    }
                }
                subscriber.onNext(json);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<AreaDescriptionMetadata> writeMetaDataJson(File file, AreaDescriptionMetadata metaData) {
        String json = mapper.toJson(metaData);
        return Observable.create(subscriber -> {
            LOG.d("Writing json meta data to '%s'", file.getPath());
            try {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write(json);
                    writer.flush();
                }
                subscriber.onNext(metaData);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    private Observable<File> deleteFile(File file) {
        file.delete();
        return Observable.just(file);
    }
}
