package com.lakeel.altla.vision.admanager.data.repository;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
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

    private final MetaDataMapper mMapper = new MetaDataMapper();

    private final File mDirectory;

    @Inject
    public AppMetaDataRepositoryImpl(File directory) {
        mDirectory = directory;
        if (!mDirectory.exists()) {
            if (!mDirectory.mkdirs()) {
                throw new IllegalStateException("Creating a directory failed: " + mDirectory);
            }
        }
    }

    @Override
    public Observable<AreaDescriptionMetaData> find(String uuid) {
        return getMetaDataJsonFile(uuid)
                .flatMap(this::readMetaDataJson)
                .map(mMapper::fromJson);
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

    Observable<File> getMetaDataJsonFile(String uuid) {
        return createMetaDataJsonFile(uuid)
                .filter(File::exists);
    }

    Observable<File> createMetaDataJsonFile(String uuid) {
        String fileName = uuid + EXTENTION;
        File file = new File(mDirectory, fileName);
        return Observable.just(file);
    }

    Observable<String> readMetaDataJson(File file) {
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

    Observable<AreaDescriptionMetaData> writeMetaDataJson(File file, AreaDescriptionMetaData metaData) {
        String json = mMapper.toJson(metaData);
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

    Observable<File> deleteFile(File file) {
        file.delete();
        return Observable.just(file);
    }
}
