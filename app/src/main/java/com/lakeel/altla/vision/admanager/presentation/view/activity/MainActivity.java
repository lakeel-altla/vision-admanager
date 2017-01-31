package com.lakeel.altla.vision.admanager.presentation.view.activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.EditUserAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.EditUserAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.domain.usecase.ObserveConnectionUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserProfileUseCase;
import com.lakeel.altla.vision.domain.usecase.SignOutUseCase;
import com.squareup.picasso.Picasso;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class MainActivity extends AppCompatActivity
        implements ActivityScopeContext,
                   SignInFragment.InteractionListener,
                   TangoPermissionFragment.InteractionListener,
                   UserAreaDescriptionListFragment.InteractionListener,
                   UserAreaListFragment.InteractionListener,
                   EditUserAreaFragment.InteractionListener,
                   NavigationView.OnNavigationItemSelectedListener {

    private static final String FRAGMENT_TAG_USER_AREA_LIST = UserAreaListFragment.class.getName();

    private static final String FRAGMENT_TAG_TANGO_AREA_DESCRIPTION_LIST =
            TangoAreaDescriptionListFragment.class.getName();

    private static final String FRAGMENT_TAG_USER_AREA_DESCRIPTION_LIST =
            UserAreaDescriptionListFragment.class.getName();

    @Inject
    TangoWrapper tangoWrapper;

    @Inject
    ObserveUserProfileUseCase observeUserProfileUseCase;

    @Inject
    ObserveConnectionUseCase observeConnectionUseCase;

    @Inject
    SignOutUseCase signOutUseCase;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ActivityComponent activityComponent;

    private NavigationViewHeader navigationViewHeader;

    private Disposable observeUserProfileDisposable;

    private Disposable observeConnectionDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent = MyApplication.getApplicationComponent(this)
                                         .activityComponent(new ActivityModule(this));
        activityComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        navigationViewHeader = new NavigationViewHeader(navigationView);

        showSignInFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(navigationViewHeader);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Unsubscribe the connection.
        if (observeConnectionDisposable != null) {
            observeConnectionDisposable.dispose();
            observeConnectionDisposable = null;
        }

        // Unsubscribe the user profile.
        if (observeUserProfileDisposable != null) {
            observeUserProfileDisposable.dispose();
            observeUserProfileDisposable = null;
        }

        compositeDisposable.clear();

        FirebaseAuth.getInstance().removeAuthStateListener(navigationViewHeader);
    }

    @Override
    public void onResume() {
        super.onResume();

        tangoWrapper.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        tangoWrapper.disconnect();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_user_area_list:
                showUserAreaListFragment();
                break;
            case R.id.nav_tango_space:
                showTangoAreaDescriptionListFragment();
                break;
            case R.id.nav_app_space:
                showUserAreaDescriptionListFragment();
                break;
            case R.id.nav_sign_out:
                onSignOut();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    @Override
    public void onShowEditUserAreaDescriptionFragment(String areaDescriptionId) {
        EditUserAreaDescriptionFragment fragment = EditUserAreaDescriptionFragment.newInstance(areaDescriptionId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onCloseSignInFragment() {
        showTangoPermissionFragment();
    }

    @Override
    public void onCloseTangoPermissionFragment() {
        showTangoAreaDescriptionListFragment();
    }

    @Override
    public void onCreateUserArea() {
        EditUserAreaFragment fragment = EditUserAreaFragment.newInstance(null);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onEditUserArea(String areaId) {
        EditUserAreaFragment fragment = EditUserAreaFragment.newInstance(areaId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    private void onSignOut() {
        Disposable disposable = signOutUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showSignInFragment);
        compositeDisposable.add(disposable);
    }

    private void showSignInFragment() {
        SignInFragment fragment = SignInFragment.newInstance();
        replaceFragment(fragment);
    }

    private void showTangoPermissionFragment() {
        TangoPermissionFragment fragment = TangoPermissionFragment.newInstance();
        replaceFragment(fragment);
    }

    private void showUserAreaListFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_USER_AREA_LIST);
        if (fragment == null) {
            fragment = UserAreaListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.fragment_container, fragment, FRAGMENT_TAG_USER_AREA_LIST)
                                       .commit();
        }
    }

    private void showTangoAreaDescriptionListFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TANGO_AREA_DESCRIPTION_LIST);
        if (fragment == null) {
            fragment = TangoAreaDescriptionListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.fragment_container, fragment,
                                                FRAGMENT_TAG_TANGO_AREA_DESCRIPTION_LIST)
                                       .commit();
        }
    }

    private void showUserAreaDescriptionListFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_USER_AREA_DESCRIPTION_LIST);
        if (fragment == null) {
            fragment = UserAreaDescriptionListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                                       .replace(R.id.fragment_container, fragment,
                                                FRAGMENT_TAG_USER_AREA_DESCRIPTION_LIST)
                                       .commit();
        }
    }

    private void replaceFragmentAndAddToBackStack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    class NavigationViewHeader implements FirebaseAuth.AuthStateListener {

        @BindView(R.id.image_view_user_photo)
        ImageView imageViewUserPhoto;

        @BindView(R.id.text_view_user_name)
        TextView textViewUserName;

        @BindView(R.id.text_view_user_email)
        TextView textViewUserEmail;

        private NavigationViewHeader(@NonNull NavigationView navigationView) {
            ButterKnife.bind(this, navigationView.getHeaderView(0));
        }

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {
                // Subscribe the connection.
                if (observeConnectionDisposable == null) {
                    observeConnectionDisposable = observeConnectionUseCase
                            .execute()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe();
                }

                // Subscribe the user profile.
                if (observeUserProfileDisposable == null) {
                    observeUserProfileDisposable = observeUserProfileUseCase
                            .execute()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(userProfile -> {
                                // Update UI each time the user profile is updated.
                                if (userProfile.photoUri != null) {
                                    Uri photoUri = Uri.parse(userProfile.photoUri);
                                    Picasso.with(MainActivity.this).load(photoUri).into(imageViewUserPhoto);
                                }
                                textViewUserName.setText(userProfile.displayName);
                                textViewUserEmail.setText(userProfile.email);
                            });
                }
            } else {
                // Unsubscribe the connection.
                if (observeConnectionDisposable != null) {
                    observeConnectionDisposable.dispose();
                    observeConnectionDisposable = null;
                }

                // Unsubscribe the user profile.
                if (observeUserProfileDisposable != null) {
                    observeUserProfileDisposable.dispose();
                    observeUserProfileDisposable = null;
                }

                // Clear UI.
                imageViewUserPhoto.setImageBitmap(null);
                textViewUserName.setText(null);
                textViewUserEmail.setText(null);
            }
        }
    }
}
