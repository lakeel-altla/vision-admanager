package com.lakeel.altla.vision.admanager.presentation.view.activity;

import com.lakeel.altla.vision.admanager.R;
import com.lakeel.altla.vision.admanager.presentation.app.MyApplication;
import com.lakeel.altla.vision.admanager.presentation.di.component.UserComponent;
import com.lakeel.altla.vision.admanager.presentation.di.module.ActivityModule;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.SignInFragment;
import com.lakeel.altla.vision.admanager.presentation.view.fragment.TangoPermissionFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity
        implements ActivityScopeContext, SignInFragment.OnShowTangoPermissionFragmentListener,
                   TangoPermissionFragment.OnStartManagerActivityListener {

    private UserComponent mUserComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Dagger
        mUserComponent = MyApplication.getApplicationComponent(this)
                                      .userComponent(new ActivityModule(this));

        SignInFragment fragment = SignInFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    @Override
    public void onShowTangoPermissionFragment() {
        TangoPermissionFragment fragment = TangoPermissionFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.fragment_container, fragment)
                                   .commit();
    }

    @Override
    public void onStartManagerActivity() {
        Intent intent = ManagerActivity.getStartActivityIntent(this);
        startActivity(intent);
    }

    public UserComponent getUserComponent() {
        return mUserComponent;
    }
}
