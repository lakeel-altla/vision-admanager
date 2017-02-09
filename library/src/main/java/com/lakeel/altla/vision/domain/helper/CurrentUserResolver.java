package com.lakeel.altla.vision.domain.helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.support.annotation.NonNull;

import javax.inject.Inject;

public final class CurrentUserResolver {

    @Inject
    public CurrentUserResolver() {
    }

    @NonNull
    public String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) throw new IllegalStateException("The user is not signed in.");

        return currentUser.getUid();
    }
}
