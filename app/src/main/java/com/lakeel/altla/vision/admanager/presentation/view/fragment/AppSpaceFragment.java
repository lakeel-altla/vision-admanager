package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.AppSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceView;
import com.lakeel.altla.vision.admanager.presentation.view.activity.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.AppSpaceAdapter;
import com.lakeel.altla.vision.admanager.presentation.view.helper.SwipeRightItemTouchHelper;
import com.lakeel.altla.vision.admanager.presentation.view.helper.TangoActivityForResult;
import com.lakeel.altla.vision.admanager.presentation.view.helper.TangoActivityForResultHost;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AppSpaceFragment extends Fragment
        implements AppSpaceView, TangoActivityForResult.OnTangoActivityResultListener {

    @Inject
    Tango mTango;

    @Inject
    AppSpacePresenter mPresenter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private ProgressDialog mProgressDialog;

    private TangoActivityForResult mTangoActivityForResult;

    public static AppSpaceFragment newInstance() {
        return new AppSpaceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Dagger
        ActivityScopeContext.class.cast(getContext()).getUserComponent().inject(this);

        mTangoActivityForResult = TangoActivityForResultHost.class.cast(getContext()).getTangoActivityForResult();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_space, container, false);
        ButterKnife.bind(this, view);

        mPresenter.onCreateView(this);

        mRecyclerView.setAdapter(new AppSpaceAdapter(mPresenter));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper helper = new SwipeRightItemTouchHelper(mPresenter::onDelete);
        helper.attachToRecyclerView(mRecyclerView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onTangoActivityResult(boolean isCanceled) {
        mTangoActivityForResult.removeOnTangoActivityForResultListener(this);

        if (!isCanceled) {
            Snackbar.make(mRecyclerView, R.string.snackbar_imported, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void updateItems() {
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateItemRemoved(@IntRange(from = 0) int position) {
        mRecyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(mRecyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showImportActivity(@NonNull String path) {
        mTangoActivityForResult.addOnTangoActivityForResultListener(this);
        mTango.importAreaDescriptionFile(path);
    }

    @Override
    public void showUploadProgressDialog() {
        // 進捗率を出す場合は、進捗率のリセットが不可能であるため、インスタンスをキャッシュすることはできない。
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_upload));
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMax(0);
        mProgressDialog.show();
    }

    @Override
    public void setUploadProgressDialogProgress(long max, long diff) {
        if (mProgressDialog != null) {
            mProgressDialog.setMax((int) max);
            mProgressDialog.incrementProgressBy((int) diff);
        }
    }

    @Override
    public void hideUploadProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}
