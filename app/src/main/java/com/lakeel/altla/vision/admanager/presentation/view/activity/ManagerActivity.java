package com.lakeel.altla.vision.admanager.presentation.view.activity;

import com.google.atap.tangoservice.TangoConfig;
import com.google.firebase.auth.FirebaseAuth;

import com.lakeel.altla.tango.TangoWrapper;
import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.ActivityScopeContext;
import com.lakeel.altla.vision.admanager.presentation.di.component.ActivityComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.AppSpaceFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoSpaceFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class ManagerActivity extends AppCompatActivity
        implements ActivityScopeContext,
                   TangoSpaceFragment.InteractionListener,
                   AppSpaceFragment.InteractionListener,
                   NavigationView.OnNavigationItemSelectedListener {

    private static final String FRAGMENT_TAG_TANGO_SPACE = TangoSpaceFragment.class.getName();

    private static final String FRAGMENT_TAG_APP_SPACE = AppSpaceFragment.class.getName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private final FragmentController fragmentController = new FragmentController();

    private TangoWrapper tangoWrapper;

    private ActivityComponent activityComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityComponent = MyApplication.getApplicationComponent(this)
                                         .activityComponent(new ActivityModule(this));
        activityComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        ButterKnife.bind(this);

        tangoWrapper = new TangoWrapper(this);
        tangoWrapper.setTangoConfigFactory(tango -> tango.getConfig(TangoConfig.CONFIG_TYPE_DEFAULT));

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        fragmentController.showTangoSpaceFragment();
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tango_space) {
            fragmentController.showTangoSpaceFragment();
        } else if (id == R.id.nav_app_space) {
            fragmentController.showAppSpaceFragment();
        } else if (id == R.id.nav_sign_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(StartActivity.getStartActivityIntent(this));
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public TangoWrapper getTangoWrapper() {
        return tangoWrapper;
    }

    public ActivityComponent getActivityComponent() {
        return activityComponent;
    }

    public static Intent getStartActivityIntent(@NonNull Context context) {
        return new Intent(context, ManagerActivity.class);
    }

    private final class FragmentController {

        TangoSpaceFragment findTangoSpaceFragment() {
            return (TangoSpaceFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_TANGO_SPACE);
        }

        AppSpaceFragment findAppSpaceFragment() {
            return (AppSpaceFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_APP_SPACE);
        }

        void showTangoSpaceFragment() {
            TangoSpaceFragment fragment = findTangoSpaceFragment();
            if (fragment == null) {
                fragment = TangoSpaceFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragment_container, fragment, FRAGMENT_TAG_TANGO_SPACE)
                                           .commit();
            }
        }

        void showAppSpaceFragment() {
            AppSpaceFragment fragment = findAppSpaceFragment();
            if (fragment == null) {
                fragment = AppSpaceFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                                           .replace(R.id.fragment_container, fragment, FRAGMENT_TAG_APP_SPACE)
                                           .commit();
            }
        }
    }
}
