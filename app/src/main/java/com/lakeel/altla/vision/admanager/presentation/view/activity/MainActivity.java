package com.lakeel.altla.vision.admanager.presentation.view.activity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.android.log.Log;
import com.lakeel.altla.android.log.LogFactory;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.helper.RxHelper;
import com.lakeel.altla.vision.admanager.presentation.service.UserImageAssetFileUploadTaskService;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionByAreaListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaDescriptionListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaListFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserAreaSelectFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserImageAssetEditFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserImageAssetFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.UserImageAssetListFragment;
import com.lakeel.altla.vision.api.VisionService;
import com.lakeel.altla.vision.helper.DataListEvent;
import com.lakeel.altla.vision.model.ImageAssetFileUploadTask;
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
import io.reactivex.Completable;
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
                   UserAreaDescriptionByAreaListFragment.InteractionListener,
                   UserAreaDescriptionListFragment.InteractionListener,
                   UserAreaDescriptionFragment.InteractionListener,
                   UserAreaDescriptionEditFragment.InteractionListener,
                   UserAreaSelectFragment.InteractionListener,
                   UserImageAssetListFragment.InteractionListener,
                   UserImageAssetFragment.InteractionListener,
                   UserImageAssetEditFragment.InteractionListener,
                   NavigationView.OnNavigationItemSelectedListener {

    private static final Log LOG = LogFactory.getLog(MainActivity.class);

    @Inject
    VisionService visionService;

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

    private Disposable observeAllUserAssetImageFileUploadTasksDisposable;

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
        if (observeAllUserAssetImageFileUploadTasksDisposable != null) {
            observeAllUserAssetImageFileUploadTasksDisposable.dispose();
            observeAllUserAssetImageFileUploadTasksDisposable = null;
        }

        compositeDisposable.clear();

        FirebaseAuth.getInstance().removeAuthStateListener(navigationViewHeader);
    }

    @Override
    public void onResume() {
        super.onResume();

        visionService.getTangoWrapper().connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        visionService.getTangoWrapper().disconnect();
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
            case R.id.nav_user_actor_image_list: {
                Fragment fragment = findFragment(UserImageAssetListFragment.class);
                if (fragment == null) {
                    fragment = UserImageAssetListFragment.newInstance();
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
        UserAreaDescriptionByAreaListFragment fragment = UserAreaDescriptionByAreaListFragment.newInstance(areaId);
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
    }

    @Override
    public void onShowUserActorImageCreateView() {
        UserImageAssetEditFragment fragment = UserImageAssetEditFragment.newInstance(null);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserActorImageView(@NonNull String imageId) {
        UserImageAssetFragment fragment = UserImageAssetFragment.newInstance(imageId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    @Override
    public void onShowUserActorImageEditView(@NonNull String imageId) {
        UserImageAssetEditFragment fragment = UserImageAssetEditFragment.newInstance(imageId);
        replaceFragmentAndAddToBackStack(fragment);
    }

    private void updateActionBarHome() {
        if (getSupportActionBar() != null) {
            boolean isHome = (getSupportFragmentManager().getBackStackEntryCount() == 0);
            drawerToggle.setDrawerIndicatorEnabled(isHome);
        }
    }

    private void onSignOut() {
        visionService.getUserDeviceConnectionApi().markUserDeviceConnectionAsOffline(aVoid -> {
            FirebaseAuth.getInstance().signOut();
            showSignInView();
        }, e -> {
            LOG.e("Failed.", e);
            FirebaseAuth.getInstance().signOut();
            showSignInView();
        });
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
                    observeConnectionDisposable = RxHelper
                            .usingData(() -> visionService.getFirebaseConnectionApi().observeConnection())
                            .doOnNext(connected -> LOG
                                    .i("The user device connection state changed: connected = %b", connected))
                            .flatMapCompletable(connected -> {
                                return Completable.create(e -> {
                                    if (connected) {
                                        visionService.getUserDeviceConnectionApi()
                                                     .markUserDeviceConnectionAsOnline(aVoid -> {
                                                         e.onComplete();
                                                     }, e::onError);
                                    } else {
                                        e.onComplete();
                                    }
                                });
                            }).subscribe();
                }

                // Subscribe the user profile.
                if (observeUserProfileDisposable == null) {
                    observeUserProfileDisposable = RxHelper
                            .usingData(() -> visionService.getUserProfileApi().observeUserProfileById(user.getUid()))
                            .subscribe(profile -> {
                                // Update UI each time the user profile is updated.
                                if (profile.getPhotoUri() != null) {
                                    Uri photoUri = Uri.parse(profile.getPhotoUri());
                                    Picasso.with(MainActivity.this).load(photoUri).into(imageViewUserPhoto);
                                }
                                textViewUserName.setText(profile.getDisplayName());
                                textViewUserEmail.setText(profile.getEmail());
                            }, e -> {
                                LOG.e("Failed.", e);
                            });
                }

                // Observe all pending storage tasks.
                if (observeAllUserAssetImageFileUploadTasksDisposable == null) {
                    observeAllUserAssetImageFileUploadTasksDisposable = RxHelper
                            .usingList(() -> visionService.getUserAssetApi().observeUserImageAssetFileUploadTask())
                            .subscribe(event -> {
                                if (event.getType() == DataListEvent.Type.ADDED) {
                                    ImageAssetFileUploadTask task = event.getData();

                                    if (!FirebaseInstanceId.getInstance().getId().equals(task.getInstanceId())) return;

                                    Intent intent = UserImageAssetFileUploadTaskService.createIntent(
                                            getApplicationContext(), task);
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
                if (observeAllUserAssetImageFileUploadTasksDisposable != null) {
                    observeAllUserAssetImageFileUploadTasksDisposable.dispose();
                    observeAllUserAssetImageFileUploadTasksDisposable = null;
                }

                // Clear UI.
                imageViewUserPhoto.setImageBitmap(null);
                textViewUserName.setText(null);
                textViewUserEmail.setText(null);
            }
        }
    }
}
