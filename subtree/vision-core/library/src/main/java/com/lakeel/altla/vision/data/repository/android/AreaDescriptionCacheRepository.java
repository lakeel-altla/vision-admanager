package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.vision.ArgumentNullException;

import java.io.File;

public final class AreaDescriptionCacheRepository {

    private static final String PATH = "areaDescriptions";

    private final File rootDirectory;

    public AreaDescriptionCacheRepository(File rootDirectory) {
        if (rootDirectory == null) throw new ArgumentNullException("rootDirectory");

        this.rootDirectory = rootDirectory;
    }

    public boolean exists(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        File file = resolveCacheFile(areaDescriptionId);
        return file.exists();
    }

    public File getDirectory() {
        return ensureCacheDirectory();
    }

    public File getFile(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        return resolveCacheFile(areaDescriptionId);
    }

    public void delete(String areaDescriptionId) {
        if (areaDescriptionId == null) throw new ArgumentNullException("areaDescriptionId");

        File file = resolveCacheFile(areaDescriptionId);
        if (file.exists()) {
            file.delete();
        }
    }

    private File ensureCacheDirectory() {
        File directory = new File(rootDirectory, PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        return directory;
    }

    private File resolveCacheFile(String areaDescriptionId) {
        File directory = ensureCacheDirectory();
        return new File(directory, areaDescriptionId);
    }
}
