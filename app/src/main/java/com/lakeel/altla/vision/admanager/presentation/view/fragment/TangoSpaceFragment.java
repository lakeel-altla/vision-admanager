package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.TangoSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.view.TangoSpaceView;
import com.lakeel.altla.vision.admanager.presentation.view.activity.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.TangoSpaceAdapter;
import com.lakeel.altla.vision.admanager.presentation.view.helper.SwipeRightItemTouchHelper;
import com.lakeel.altla.vision.admanager.presentation.view.helper.TangoActivityForResult;
import com.lakeel.altla.vision.admanager.presentation.view.helper.TangoActivityForResultHost;

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

public class TangoSpaceFragment extends Fragment
        implements TangoSpaceView, TangoActivityForResult.OnTangoActivityResultListener {

    @Inject
    Tango mTango;

    @Inject
    TangoSpacePresenter mPresenter;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private TangoActivityForResult mTangoActivityForResult;

    public static TangoSpaceFragment newInstance() {
        return new TangoSpaceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // Dagger
        ActivityScopeContext.class.cast(getContext()).getUserComponent().inject(this);

        mTangoActivityForResult = TangoActivityForResultHost.class.cast(context).getTangoActivityForResult();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tango_space, container, false);
        ButterKnife.bind(this, view);

        mPresenter.onCreateView(this);

        mRecyclerView.setAdapter(new TangoSpaceAdapter(mPresenter));
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
            mPresenter.exportMetaData();
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
    public void showExportActivity(@NonNull String uuid, @NonNull String directory) {
        mTangoActivityForResult.addOnTangoActivityForResultListener(this);
        mTango.exportAreaDescriptionFile(uuid, directory);
    }
}
