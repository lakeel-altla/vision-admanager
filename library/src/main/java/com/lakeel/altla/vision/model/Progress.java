package com.lakeel.altla.vision.model;

public final class Progress {

    public final long totalBytes;

    public final long bytesTransferred;

    public Progress(long totalBytes, long bytesTransferred) {
        this.totalBytes = totalBytes;
        this.bytesTransferred = bytesTransferred;
    }
}
