package com.lakeel.altla.vision.admanager.presentation.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

class ActivityForResult {

    private Activity activity;

    private Fragment fragment;

    private Intent intent;

    private int requestCode;

    private OnActivityResultListener listener;

    protected ActivityForResult(Activity activity, Fragment fragment, Intent intent, int requestCode,
                                OnActivityResultListener listener) {
        this.activity = activity;
        this.fragment = fragment;
        this.intent = intent;
        this.requestCode = requestCode;
        this.listener = listener;
    }

    public void start() {
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        } else {
            fragment.startActivityForResult(intent, requestCode);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.requestCode != requestCode) {
            return false;
        }

        if (Activity.RESULT_OK == resultCode && listener != null) {
            listener.onActivityResult(data, false);
        } else if (Activity.RESULT_CANCELED == resultCode && listener != null) {
            listener.onActivityResult(data, true);
        }

        return true;
    }

    public interface OnActivityResultListener {

        void onActivityResult(Intent intent, boolean isCanceled);
    }

    public static class Builder {

        private Activity activity;

        private Fragment fragment;

        private Intent intent;

        private int requestCode;

        private OnActivityResultListener listener;

        public Builder(@NonNull Activity activity, @NonNull Intent intent) {
            this.activity = activity;
            this.intent = intent;
        }

        public Builder(@NonNull Fragment fragment, @NonNull Intent intent) {
            this.fragment = fragment;
            this.intent = intent;
        }

        @NonNull
        public final Builder setRequestCode(int requestCode) {
            this.requestCode = requestCode;
            return this;
        }

        @NonNull
        public final Builder setListener(OnActivityResultListener listener) {
            this.listener = listener;
            return this;
        }

        @NonNull
        public final ActivityForResult build() {
            return new ActivityForResult(activity, fragment, intent, requestCode, listener);
        }
    }
}
