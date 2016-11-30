package com.lakeel.altla.vision.admanager.presentation.view.activity;

import com.google.atap.tangoservice.Tango;
import com.google.atap.tangoservice.TangoConfig;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.component.UserComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.AppSpaceFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoSpaceFragment;
import com.lakeel.altla.vision.admanager.presentation.view.helper.TangoActivityForResult;
import com.lakeel.altla.vision.admanager.presentation.view.helper.TangoActivityForResultHost;

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

public class ManagerActivity extends AppCompatActivity
        implements ActivityScopeContext, TangoActivityForResultHost, NavigationView.OnNavigationItemSelectedListener {

    @Inject
    Tango mTango;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private final TangoActivityForResult mTangoActivityForResult = new TangoActivityForResult();

    private FragmentController mFragmentController;

    private UserComponent mUserComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mUserComponent = MyApplication.getApplicationComponent(this)
                                      .userComponent(new ActivityModule(this));
        mUserComponent.inject(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mFragmentController = new FragmentController(getSupportFragmentManager());
        mFragmentController.showTangoSpaceAdListFragment();
    }

    @Override
    public void onResume() {
        super.onResume();

        TangoConfig config = mTango.getConfig(TangoConfig.CONFIG_TYPE_CURRENT);
        mTango.connect(config);
    }

    @Override
    public void onPause() {
        super.onPause();

        synchronized (this) {
            mTango.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
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
            mFragmentController.showTangoSpaceAdListFragment();
        } else if (id == R.id.nav_app_space) {
            mFragmentController.showAppSpaceAdListFragment();
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mTangoActivityForResult.onResult(requestCode, resultCode, data);
    }

    public UserComponent getUserComponent() {
        return mUserComponent;
    }

    public TangoActivityForResult getTangoActivityForResult() {
        return mTangoActivityForResult;
    }

    public static Intent getStartActivityIntent(@NonNull Context context) {
        return new Intent(context, ManagerActivity.class);
    }

    private static class FragmentController {

        private static final String FRAGMENT_TAG_TANGO_SPACE_AD_LIST = TangoSpaceFragment.class.getName();

        private static final String FRAGMENT_TAG_APP_SPACE_AD_LIST = AppSpaceFragment.class.getName();

        private FragmentManager mManager;

        public FragmentController(@NonNull FragmentManager manager) {
            mManager = manager;
        }

        public TangoSpaceFragment findTangoSpaceAdListFragment() {
            return (TangoSpaceFragment) mManager.findFragmentByTag(FRAGMENT_TAG_TANGO_SPACE_AD_LIST);
        }

        public AppSpaceFragment findAppSpaceAdListFragment() {
            return (AppSpaceFragment) mManager.findFragmentByTag(FRAGMENT_TAG_APP_SPACE_AD_LIST);
        }

        public void showTangoSpaceAdListFragment() {
            TangoSpaceFragment fragment = findTangoSpaceAdListFragment();
            if (fragment == null) {
                fragment = TangoSpaceFragment.newInstance();
                mManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment, FRAGMENT_TAG_TANGO_SPACE_AD_LIST)
                        .commit();
            }
        }

        public void showAppSpaceAdListFragment() {
            AppSpaceFragment fragment = findAppSpaceAdListFragment();
            if (fragment == null) {
                fragment = AppSpaceFragment.newInstance();
                mManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment, FRAGMENT_TAG_APP_SPACE_AD_LIST)
                        .commit();
            }
        }
    }
}
