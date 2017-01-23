package com.lakeel.altla.vision.data.repository.android;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.ArgumentNullException;

import android.content.Context;

import java.io.File;
import java.io.IOException;

public final class TextureCacheRepository {

    private static final Log LOG = LogFactory.getLog(TextureCacheRepository.class);

    private final Context context;

    public TextureCacheRepository(Context context) {
        if (context == null) throw new ArgumentNullException("context");

        this.context = context;
    }

    public File find(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        File file = resolveCacheFile(textureId);
        if (file.exists()) {
            LOG.d("The cache file exists: textureId = %s", textureId);
            return file;
        } else {
            return null;
        }
    }

    public File create(String textureId) throws IOException {
        if (textureId == null) throw new ArgumentNullException("textureId");

        File file = resolveCacheFile(textureId);
        if (file.createNewFile()) {
            LOG.d("Created the new cache file: textureId = %s", textureId);
        } else {
            LOG.w("The cache file already exists: textureId = %s", textureId);
        }
        return file;
    }

    public void delete(String textureId) {
        if (textureId == null) throw new ArgumentNullException("textureId");

        File file = resolveCacheFile(textureId);
        if (file.delete()) {
            LOG.d("Deleted the new cache file: textureId = %s", textureId);
        } else {
            LOG.w("The cache file does not exist: textureId = %s", textureId);
        }
    }

    private File resolveCacheFile(String textureId) {
        File directory = new File(context.getCacheDir(), "textures");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return new File(directory, textureId);
    }
}
