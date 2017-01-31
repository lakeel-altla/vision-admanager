package com.lakeel.altla.vision.admanager.presentation.view.fragment;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.presenter.EditUserAreaPresenter;
import com.lakeel.altla.vision.admanager.presentation.presenter.model.EditUserAreaModel;
import com.lakeel.altla.vision.admanager.presentation.view.EditUserAreaView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.OnTextChanged;

public final class EditUserAreaFragment extends Fragment implements EditUserAreaView {

    private static final String ARG_AREA_ID = "areaId";

    private static final int REQUEST_CODE_PLACE_PICKER = 1;

    private static final int LEVEL_MIN = -100;

    private static final int LEVEL_MAX = 100;

    private static final List<Integer> LEVELS;

    @Inject
    EditUserAreaPresenter presenter;

    @Inject
    AppCompatActivity activity;

    @Inject
    GoogleApiClient googleApiClient;

    @BindView(R.id.view_top)
    View viewTop;

    @BindView(R.id.text_view_id)
    TextView textViewId;

    @BindView(R.id.text_view_created_at)
    TextView textViewCreatedAt;

    @BindView(R.id.text_input_layput_name)
    TextInputLayout textInputLayoutName;

    @BindView(R.id.image_button_pick_place)
    ImageButton imageButtonPickPlace;

    @BindView(R.id.text_input_edit_text_name)
    TextInputEditText textInputEditTextName;

    @BindView(R.id.text_view_place_name)
    TextView textViewPlaceName;

    @BindView(R.id.text_view_place_address)
    TextView textViewPlaceAddress;

    @BindView(R.id.spinner_level)
    Spinner spinnerLevel;

    private InteractionListener interactionListener;

    static {
        List<Integer> levelValues = new ArrayList<>();
        for (int i = LEVEL_MIN; i <= LEVEL_MAX; i++) {
            levelValues.add(i);
        }
        LEVELS = Collections.unmodifiableList(levelValues);
    }

    @NonNull
    public static EditUserAreaFragment newInstance(@Nullable String areaId) {
        EditUserAreaFragment fragment = new EditUserAreaFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_AREA_ID, areaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        interactionListener = InteractionListener.class.cast(context);
        ActivityScopeContext.class.cast(context).getActivityComponent().inject(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String areaId = null;
        if (getArguments() != null) {
            areaId = getArguments().getString(ARG_AREA_ID, null);
        }

        presenter.onCreate(areaId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_user_area, container, false);
        ButterKnife.bind(this, view);
        presenter.onCreateView(this);

        // Close the soft keyboard when the user taps enter key.
        textInputEditTextName.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(LEVELS);
        spinnerLevel.setAdapter(adapter);

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PLACE_PICKER) {
            imageButtonPickPlace.setEnabled(true);

            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(getContext(), data);
                presenter.onPlacePicked(place);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showAreaId(String areaId) {
        textViewId.setText(areaId);
    }

    @Override
    public void showCreatedAt(long createdAt) {
        String createdAtString = null;
        if (0 < createdAt) {
            createdAtString = DateFormat.getDateFormat(getContext()).format(createdAt) + " " +
                              DateFormat.getTimeFormat(getContext()).format(createdAt);
        }
        textViewCreatedAt.setText(createdAtString);
    }

    @Override
    public void showModel(@NonNull EditUserAreaModel model) {
        showAreaId(model.areaId);
        showCreatedAt(model.createdAt);

        textInputEditTextName.setText(model.name);
        textViewPlaceName.setText(model.placeName);
        textViewPlaceAddress.setText(model.placeAddress);

        int position = LEVELS.indexOf(model.level);
        spinnerLevel.setSelection(position);
    }

    @Override
    public void showSnackbar(@StringRes int resId) {
        Snackbar.make(viewTop, resId, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void showPlacePicker() {
        if (googleApiClient.isConnected()) {
            imageButtonPickPlace.setEnabled(false);
            try {
                Intent intent = new PlacePicker.IntentBuilder().build(activity);
                startActivityForResult(intent, REQUEST_CODE_PLACE_PICKER);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                showSnackbar(R.string.snackbar_failed);
                imageButtonPickPlace.setEnabled(true);
            }
        }

    }

    @OnTextChanged(value = R.id.text_input_edit_text_name, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onEditTextNameAfterTextChanged(Editable editable) {
        presenter.onEditTextNameAfterTextChanged(editable.toString());
    }

    @OnClick(R.id.image_button_pick_place)
    void onClickImageButtonPickPlace() {
        presenter.onClickImageButtonPickPlace();
    }

    @OnClick(R.id.image_button_remove_place)
    void onClickImageButtonRemovePlace() {
        presenter.onClickImageButtonRemovePlace();
    }

    @OnItemSelected(R.id.spinner_level)
    void onItemSelectedSpinnerLevel(AdapterView<?> parent, View view, int position, long id) {
        int level = (Integer) spinnerLevel.getSelectedItem();
        presenter.onItemSelectedSpinnerLevel(level);
    }

    public interface InteractionListener {

    }
}
