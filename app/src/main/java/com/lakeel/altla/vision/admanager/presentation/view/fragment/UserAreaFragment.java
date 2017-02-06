package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.presenter.UserAreaPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.UserAreaModel;
import com.lakeel.altla.vision.admanager.presentation.view.UserAreaView;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public final class UserAreaFragment extends AbstractFragment<UserAreaView, UserAreaPresenter>
        implements UserAreaView {

    @Inject
    UserAreaPresenter presenter;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_view_name)
    TextView textViewName;

    @BindView(R.id.text_view_place_name)
    TextView textViewPlaceName;

    @BindView(R.id.text_view_place_address)
    TextView textViewPlaceAddress;

    @BindView(R.id.text_view_level)
    TextView textViewLevel;

    private InteractionListener interactionListener;

    @NonNull
    public static UserAreaFragment newInstance(@NonNull String areaId) {
        UserAreaFragment fragment = new UserAreaFragment();
        Bundle bundle = UserAreaPresenter.createArguments(areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public UserAreaPresenter getPresenter() {
        return presenter;
    }

    @Override
    protected UserAreaView getViewInterface() {
        return this;
    }

    @Override
    protected void onInject(@NonNull ActivityComponent component) {
        super.onInject(component);

        component.inject(this);
    }

    @Override
    protected void onAttachOverride(@NonNull Context context) {
        super.onAttachOverride(context);

        interactionListener = InteractionListener.class.cast(context);
    }

    @Override
    protected void onDetachOverride() {
        super.onDetachOverride();

        interactionListener = null;
    }

    @Nullable
    @Override
    protected View onCreateViewCore(LayoutInflater inflater, @Nullable ViewGroup container,
                                    @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_area, container, false);
    }

    @Override
    protected void onBindView(@NonNull View view) {
        super.onBindView(view);

        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);

        // Reset the title of the previous view.
        getActivity().setTitle(null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_user_area, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                presenter.onEdit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onModelUpdated(@NonNull UserAreaModel model) {
        textViewId.setText(model.areaId);
        String createdAtString = null;
        if (0 < model.createdAt) {
            createdAtString = DateFormat.getDateFormat(getContext()).format(model.createdAt) + " " +
                              DateFormat.getTimeFormat(getContext()).format(model.createdAt);
        }
        textViewCreatedAt.setText(createdAtString);
        textViewName.setText(model.name);
        textViewPlaceName.setText(model.placeName);
        textViewPlaceAddress.setText(model.placeAddress);
        textViewLevel.setText(String.valueOf(model.level));

        getActivity().setTitle(model.name);
    }

    @Override
    public void onShowUserAreaEditView(@NonNull String areaId) {
        interactionListener.onShowUserAreaEditView(areaId);
    }

    @Override
    public void onShowUserAreaDescriptionsInAreaView(@NonNull String areaId) {
        interactionListener.onShowUserAreaDescriptionsInAreaView(areaId);
    }

    @Override
    public void onSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_user_area_descriptions_in_area)
    void onClickButtonUserAreaDescriptionsInArea() {
        presenter.onClickButtonUserAreaDescriptionsInArea();
    }

    public interface InteractionListener {

        void onShowUserAreaEditView(@NonNull String areaId);

        void onShowUserAreaDescriptionsInAreaView(@NonNull String areaId);
    }
}
