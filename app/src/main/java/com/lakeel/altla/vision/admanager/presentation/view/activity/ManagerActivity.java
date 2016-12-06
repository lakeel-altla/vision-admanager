package com.lakeel.altla.vision.admanager.presentation.view.activity;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.component.UserComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.AppSpaceFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoSpaceFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class ManagerActivity extends AppCompatActivity
        implements ActivityScopeContext, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    Tango tango;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private FragmentController fragmentController;

    private UserComponent userComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userComponent = MyApplication.getApplicationComponent(this)
                                     .userComponent(new ActivityModule(this));
        userComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        fragmentController = new FragmentController(getSupportFragmentManager());
        fragmentController.showTangoSpaceAdListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        TangoConfig config = tango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        tango.connect(config);
    }

    @Override
    public void onPause() {
        super.onPause();

        synchronized (this) {
            tango.disconnect();
        }
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
            fragmentController.showTangoSpaceAdListFragment();
        } else if (id == R.id.nav_app_space) {
            fragmentController.showAppSpaceAdListFragment();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public UserComponent getUserComponent() {
        return userComponent;
    }

    public static Intent getStartActivityIntent(@NonNull Context context) {
        return new Intent(context, ManagerActivity.class);
    }

    private static class FragmentController {

        private static final String FRAGMENT_TAG_TANGO_SPACE_AD_LIST = TangoSpaceFragment.class.getName();

        private static final String FRAGMENT_TAG_APP_SPACE_AD_LIST = AppSpaceFragment.class.getName();

        private FragmentManager fragmentManager;

        public FragmentController(@NonNull FragmentManager manager) {
            fragmentManager = manager;
        }

        public TangoSpaceFragment findTangoSpaceAdListFragment() {
            return (TangoSpaceFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG_TANGO_SPACE_AD_LIST);
        }

        public AppSpaceFragment findAppSpaceAdListFragment() {
            return (AppSpaceFragment) fragmentManager.findFragmentByTag(FRAGMENT_TAG_APP_SPACE_AD_LIST);
        }

        public void showTangoSpaceAdListFragment() {
            TangoSpaceFragment fragment = findTangoSpaceAdListFragment();
            if (fragment == null) {
                fragment = TangoSpaceFragment.newInstance();
                fragmentManager.beginTransaction()
                               .replace(R.id.fragment_container, fragment, FRAGMENT_TAG_TANGO_SPACE_AD_LIST)
                               .commit();
            }
        }

        public void showAppSpaceAdListFragment() {
            AppSpaceFragment fragment = findAppSpaceAdListFragment();
            if (fragment == null) {
                fragment = AppSpaceFragment.newInstance();
                fragmentManager.beginTransaction()
                               .replace(R.id.fragment_container, fragment, FRAGMENT_TAG_APP_SPACE_AD_LIST)
                               .commit();
            }
        }
    }
}
