package com.lakeel.altla.vision.admanager.presentation.view.activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.service.UploadActorImageFileTaskService;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserActorImageEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserActorImageFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserActorImageListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListInAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaSelectFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserSceneListInAreaFragment;
import com.lakeel.altla.vision.domain.helper.CurrentApplicationResolver;
import com.lakeel.altla.vision.domain.helper.CurrentDeviceResolver;
import com.lakeel.altla.vision.domain.helper.CurrentUserResolver;
import com.lakeel.altla.vision.domain.helper.DataListEvent;
import com.lakeel.altla.vision.domain.model.UploadUserActorImageFileTask;
import com.lakeel.altla.vision.domain.usecase.ObserveAllUploadActorImageFileTasksUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveConnectionUseCase;
import com.lakeel.altla.vision.domain.usecase.ObserveUserProfileUseCase;
import com.lakeel.altla.vision.domain.usecase.SignOutUseCase;
import com.squareup.picasso.Picasso;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public final class MainActivity extends AppCompatActivity
        implements FragmentManager.OnBackStackChangedListener,
                   ActivityScopeContext,
                   SignInFragment.InteractionListener,
                   TangoPermissionFragment.InteractionListener,
                   TangoAreaDescriptionListFragment.InteractionListener,
                   TangoAreaDescriptionFragment.InteractionListener,
                   UserAreaListFragment.InteractionListener,
                   UserAreaFragment.InteractionListener,
                   UserAreaEditFragment.InteractionListener,
                   UserAreaDescriptionListInAreaFragment.InteractionListener,
                   UserSceneListInAreaFragment.InteractionListener,
                   UserAreaDescriptionListFragment.InteractionListener,
                   UserAreaDescriptionFragment.InteractionListener,
                   UserAreaDescriptionEditFragment.InteractionListener,
                   UserAreaSelectFragment.InteractionListener,
                   UserSceneListFragment.InteractionListener,
                   UserSceneFragment.InteractionListener,
                   UserSceneEditFragment.InteractionListener,
                   UserActorImageListFragment.InteractionListener,
                   UserActorImageFragment.InteractionListener,
                   UserActorImageEditFragment.InteractionListener,
                   NavigationView.OnNavigationItemSelectedListener {

    private static final Log LOG = LogFactory.getLog(MainActivity.class);

    @Inject
    TangoWrapper tangoWrapper;

    @Inject
    ObserveUserProfileUseCase observeUserProfileUseCase;

    @Inject
    ObserveConnectionUseCase observeConnectionUseCase;

    @Inject
    ObserveAllUploadActorImageFileTasksUseCase observeAllUploadActorImageFileTasksUseCase;

    @Inject
    SignOutUseCase signOutUseCase;

    @Inject
    CurrentApplicationResolver currentApplicationResolver;

    @Inject
    CurrentDeviceResolver currentDeviceResolver;

    @Inject
    CurrentUserResolver currentUserResolver;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private ActionBarDrawerToggle drawerToggle;

    private ActivityComponent activityComponent;

    private NavigationViewHeader navigationViewHeader;

    private Disposable observeUserProfileDisposable;

    private Disposable observeConnectionDisposable;

    private Disposable observeAllPendingStorageTasksDisposable;

    @NonNull
    public static Intent createStartIntent(@NonNull Context context) {
        return new Intent(context, MainActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent = MyApplication.getApplicationComponent(this)
                                         .activityComponent(new ActivityModule(this));
        activityComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        // setDisplayHomeAsUpEnabled(true) will cause the arrow icon to appear instead of the hamburger icon
        // by calling drawerToggle.setDrawerIndicatorEnabled(false).
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Use the following constructor to set the Toolbar as the ActionBar of an Activity.
        drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        updateActionBarHome();

        navigationView.setNavigationItemSelectedListener(this);
        navigationViewHeader = new NavigationViewHeader(navigationView);

        if (savedInstanceState == null) {
            showSignInView();
        }
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

        // Unsubscribe pending storage tasks.
        if (observeAllPendingStorageTasksDisposable != null) {
            observeAllPendingStorageTasksDisposable.dispose();
            observeAllPendingStorageTasksDisposable = null;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (0 < getSupportFragmentManager().getBackStackEntryCount()) {
                    getSupportFragmentManager().popBackStack();
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_user_area_list: {
                Fragment fragment = findFragment(UserAreaListFragment.class);
                if (fragment == null) {
                    fragment = UserAreaListFragment.newInstance();
                    replaceFragment(fragment);
                }
                break;
            }
            case R.id.nav_tango_area_description_list: {
                Fragment fragment = findFragment(TangoAreaDescriptionListFragment.class);
                if (fragment == null) {
                    fragment = TangoAreaDescriptionListFragment.newInstance();
                    replaceFragment(fragment);
                }
                break;
            }
            case R.id.nav_user_area_description_list: {
                Fragment fragment = findFragment(UserAreaDescriptionListFragment.class);
                if (fragment == null) {
                    fragment = UserAreaDescriptionListFragment.newInstance();
                    replaceFragment(fragment);
                }
                break;
            }
            case R.id.nav_user_scene_list: {
                Fragment fragment = findFragment(UserSceneListFragment.class);
                if (fragment == null) {
                    fragment = UserSceneListFragment.newInstance();
                    replaceFragment(fragment);
                }
                break;
            }
            case R.id.nav_user_actor_image_list: {
                Fragment fragment = findFragment(UserActorImageListFragment.class);
                if (fragment == null) {
                    fragment = UserActorImageListFragment.newInstance();
                    replaceFragment(fragment);
                }
                break;
            }
            case R.id.nav_sign_out:
                onSignOut();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackStackChanged() {
        updateActionBarHome();
    }

    @Override
    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    @Override
    public void onBackView() {
        onBackPressed();
    }

    @Override
    public void onInvalidateOptionsMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public void onCloseSignInView() {
        TangoPermissionFragment fragment = TangoPermissionFragment.newInstance();
        replaceFragment(fragment);
    }

    @Override
    public void onCloseTangoPermissionView() {
        toolbar.setVisibility(View.VISIBLE);
        TangoAreaDescriptionListFragment fragment = TangoAreaDescriptionListFragment.newInstance();
        replaceFragment(fragment);
    }

    @Override
    public void onShowTangoAreaDescriptionView(@NonNull String areaDescriptionId) {
        TangoAreaDescriptionFragment fragment = TangoAreaDescriptionFragment.newInstance(areaDescriptionId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onCloseTangoAreaDescriptionView() {
        onBackPressed();
    }

    @Override
    public void onShowUserAreaDescriptionView(@NonNull String areaDescriptionId) {
        UserAreaDescriptionFragment fragment = UserAreaDescriptionFragment.newInstance(areaDescriptionId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserAreaDescriptionEditView(@NonNull String areaDescriptionId) {
        UserAreaDescriptionEditFragment fragment = UserAreaDescriptionEditFragment.newInstance(areaDescriptionId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserAreaSelectView() {
        UserAreaSelectFragment fragment = UserAreaSelectFragment.newInstance();
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserAreaCreateView() {
        UserAreaEditFragment fragment = UserAreaEditFragment.newInstance(null);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserAreaView(@NonNull String areaId) {
        UserAreaFragment fragment = UserAreaFragment.newInstance(areaId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserAreaEditView(@NonNull String areaId) {
        UserAreaEditFragment fragment = UserAreaEditFragment.newInstance(areaId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserAreaDescriptionListInAreaView(@NonNull String areaId) {
        UserAreaDescriptionListInAreaFragment fragment = UserAreaDescriptionListInAreaFragment.newInstance(areaId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserSceneListInAreaView(@NonNull String areaId) {
        UserSceneListInAreaFragment fragment = UserSceneListInAreaFragment.newInstance(areaId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserSceneCreateView() {
        UserSceneEditFragment fragment = UserSceneEditFragment.newInstance(null);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserSceneView(@NonNull String sceneId) {
        UserSceneFragment fragment = UserSceneFragment.newInstance(sceneId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserSceneEditView(@NonNull String sceneId) {
        UserSceneEditFragment fragment = UserSceneEditFragment.newInstance(sceneId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onUserAreaSelected(@NonNull String areaId) {
        getSupportFragmentManager().popBackStack();

        // if you should back to the user area description edit view.
        UserAreaDescriptionEditFragment userAreaDescriptionEditFragment =
                (UserAreaDescriptionEditFragment) findFragment(UserAreaDescriptionEditFragment.class);
        if (userAreaDescriptionEditFragment != null) {
            userAreaDescriptionEditFragment.onUserAreaSelected(areaId);
        }

        // if you should back to the user scene edit view.
        UserSceneEditFragment userSceneEditFragment = (UserSceneEditFragment) findFragment(UserSceneEditFragment.class);
        if (userSceneEditFragment != null) {
            userSceneEditFragment.onUserAreaSelected(areaId);
        }
    }

    @Override
    public void onShowUserActorImageCreateView() {
        UserActorImageEditFragment fragment = UserActorImageEditFragment.newInstance(null);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserActorImageView(@NonNull String imageId) {
        UserActorImageFragment fragment = UserActorImageFragment.newInstance(imageId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserActorImageEditView(@NonNull String imageId) {
        UserActorImageEditFragment fragment = UserActorImageEditFragment.newInstance(imageId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    private void updateActionBarHome() {
        if (getSupportActionBar() != null) {
            boolean isHome = (getSupportFragmentManager().getBackStackEntryCount() == 0);
            drawerToggle.setDrawerIndicatorEnabled(isHome);
        }
    }

    private void onSignOut() {
        Disposable disposable = signOutUseCase
                .execute()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::showSignInView);
        compositeDisposable.add(disposable);
    }

    private void showSignInView() {
        toolbar.setVisibility(View.INVISIBLE);

        SignInFragment fragment = SignInFragment.newInstance();
        replaceFragment(fragment);
    }

    private Fragment findFragment(Class<?> clazz) {
        return getSupportFragmentManager().findFragmentByTag(clazz.getName());
    }

    private void replaceFragmentAndAddToBackStack(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .addToBackStack(null)
                                   .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
                                   .commit();
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment, fragment.getClass().getName())
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

                // Observe all pending storage tasks.
                if (observeAllPendingStorageTasksDisposable == null) {
                    observeAllPendingStorageTasksDisposable = observeAllUploadActorImageFileTasksUseCase
                            .execute()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(event -> {
                                if (event.getType() == DataListEvent.Type.ADDED) {
                                    UploadUserActorImageFileTask task = event.getData();

                                    if (!currentDeviceResolver.getInstanceId().equals(task.instanceId)) return;

                                    Intent intent =
                                            UploadActorImageFileTaskService.createIntent(getApplicationContext(), task);
                                    startService(intent);
                                }
                            }, e -> {
                                LOG.e("Failed.", e);
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

                // Unsubscribe pending storage tasks.
                if (observeAllPendingStorageTasksDisposable != null) {
                    observeAllPendingStorageTasksDisposable.dispose();
                    observeAllPendingStorageTasksDisposable = null;
                }

                // Clear UI.
                imageViewUserPhoto.setImageBitmap(null);
                textViewUserName.setText(null);
                textViewUserEmail.setText(null);
            }
        }
    }
}
