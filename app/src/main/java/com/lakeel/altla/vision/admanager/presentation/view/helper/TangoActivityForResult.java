package com.lakeel.altla.vision.admanager.presentation.view.helper;

import com.google.atap.tangoservice.Tango;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public final class TangoActivityForResult {

    private final List<OnTangoActivityResultListener> mListeners = new ArrayList<>();

    public boolean onResult(int requestCode, int resultCode, Intent data) {
        if (Tango.TANGO_INTENT_ACTIVITYCODE != requestCode) {
            return false;
        }

        boolean isCanceled = (Activity.RESULT_OK != resultCode);
        for (OnTangoActivityResultListener listener : mListeners) {
            listener.onTangoActivityResult(isCanceled);
        }

        return true;
    }

    public void addOnTangoActivityForResultListener(@NonNull OnTangoActivityResultListener listener) {
        mListeners.add(listener);
    }

    public void removeOnTangoActivityForResultListener(@NonNull OnTangoActivityResultListener listener) {
        mListeners.remove(listener);
    }

    public interface OnTangoActivityResultListener {

        void onTangoActivityResult(boolean isCanceled);
    }
}
