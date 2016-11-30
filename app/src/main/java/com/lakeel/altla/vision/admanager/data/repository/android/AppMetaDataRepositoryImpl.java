package com.lakeel.altla.vision.admanager.data.repository.android;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.data.repository.mapper.MetaDataMapper;
import com.lakeel.altla.vision.admanager.domain.model.AreaDescriptionMetaData;
import com.lakeel.altla.vision.admanager.domain.repository.AppMetaDataRepository;

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

public final class AppMetaDataRepositoryImpl implements AppMetaDataRepository {

    private static final Log LOG = LogFactory.getLog(AppMetaDataRepositoryImpl.class);

    private static final String EXTENTION = ".json";

    private final MetaDataMapper mapper = new MetaDataMapper();

    private final File baseDirectory;

    @Inject
    public AppMetaDataRepositoryImpl(File baseDirectory) {
        this.baseDirectory = baseDirectory;
        if (!this.baseDirectory.exists()) {
            if (!this.baseDirectory.mkdirs()) {
                throw new IllegalStateException("Creating a directory failed: " + this.baseDirectory);
            }
        }
    }

    @Override
    public Observable<AreaDescriptionMetaData> find(String uuid) {
        return getMetaDataJsonFile(uuid)
                .flatMap(this::readMetaDataJson)
                .map(mapper::fromJson);
    }

    @Override
    public Single<AreaDescriptionMetaData> save(AreaDescriptionMetaData metaData) {
        return createMetaDataJsonFile(metaData.uuid)
                .flatMap(file -> writeMetaDataJson(file, metaData))
                .toSingle();
    }

    @Override
    public Single<String> delete(String uuid) {
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

    private Observable<AreaDescriptionMetaData> writeMetaDataJson(File file, AreaDescriptionMetaData metaData) {
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
