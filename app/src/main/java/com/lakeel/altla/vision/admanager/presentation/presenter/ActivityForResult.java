package com.lakeel.altla.vision.admanager.presentation.presenter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

class ActivityForResult {

    private Activity mActivity;

    private Fragment mFragment;

    private Intent mIntent;

    private int mRequestCode;

    private OnActivityResultListener mListener;

    protected ActivityForResult(Activity activity, Fragment fragment, Intent intent, int requestCode,
                                OnActivityResultListener listener) {
        mActivity = activity;
        mFragment = fragment;
        mIntent = intent;
        mRequestCode = requestCode;
        mListener = listener;
    }

    public void start() {
        if (mActivity != null) {
            mActivity.startActivityForResult(mIntent, mRequestCode);
        } else {
            mFragment.startActivityForResult(mIntent, mRequestCode);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mRequestCode != requestCode) {
            return false;
        }

        if (Activity.RESULT_OK == resultCode && mListener != null) {
            mListener.onActivityResult(data, false);
        } else if (Activity.RESULT_CANCELED == resultCode && mListener != null) {
            mListener.onActivityResult(data, true);
        }

        return true;
    }

    public interface OnActivityResultListener {

        void onActivityResult(Intent intent, boolean isCanceled);
    }

    public static class Builder {

        private Activity mActivity;

        private Fragment mFragment;

        private Intent mIntent;

        private int mRequestCode;

        private OnActivityResultListener mListener;

        public Builder(@NonNull Activity activity, @NonNull Intent intent) {
            mActivity = activity;
            mIntent = intent;
        }

        public Builder(@NonNull Fragment fragment, @NonNull Intent intent) {
            mFragment = fragment;
            mIntent = intent;
        }

        @NonNull
        public final Builder setRequestCode(int requestCode) {
            mRequestCode = requestCode;
            return this;
        }

        @NonNull
        public final Builder setListener(OnActivityResultListener listener) {
            mListener = listener;
            return this;
        }

        @NonNull
        public final ActivityForResult build() {
            return new ActivityForResult(mActivity, mFragment, mIntent, mRequestCode, mListener);
        }
    }
}
