package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.google.atap.tangoservice.Tango;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoIntents;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.presenter.AppSpacePresenter;
import com.lakeel.altla.vision.admanager.presentation.view.AppSpaceView;
import com.lakeel.altla.vision.admanager.presentation.view.activity.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.view.adapter.AppSpaceAdapter;
import com.lakeel.altla.vision.admanager.presentation.view.helper.SwipeRightItemTouchHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.io.File;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class AppSpaceFragment extends Fragment implements AppSpaceView {

    private static final Log LOG = LogFactory.getLog(AppSpaceFragment.class);

    @Inject
    Tango tango;

    @Inject
    AppSpacePresenter presenter;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private ProgressDialog progressDialog;

    public static AppSpaceFragment newInstance() {
        return new AppSpaceFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        ActivityScopeContext.class.cast(getContext()).getUserComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_space, container, false);
        ButterKnife.bind(this, view);

        presenter.onCreateView(this);

        recyclerView.setAdapter(new AppSpaceAdapter(presenter));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemTouchHelper helper = new SwipeRightItemTouchHelper(presenter::onDelete);
        helper.attachToRecyclerView(recyclerView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LOG.d("onActivityResult: requestCode = %d, resultCode = %d, inrent = %s", requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            presenter.onImported();
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }

    }

    @Override
    public void updateItems() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void updateItemRemoved(int position) {
        recyclerView.getAdapter().notifyItemRemoved(position);
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(recyclerView, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showImportActivity(@NonNull File destinationFile) {
        Intent intent = TangoIntents.createAdfImportIntent(destinationFile.getPath());
        startActivityForResult(intent, 0);
    }
}
