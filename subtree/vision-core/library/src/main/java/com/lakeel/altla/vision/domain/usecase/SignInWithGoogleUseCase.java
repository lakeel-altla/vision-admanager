package com.lakeel.altla.vision.domain.usecase;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import com.lakeel.altla.vision.data.repository.firebase.UserDeviceRepository;
import com.lakeel.altla.vision.data.repository.firebase.UserProfileRepository;
import com.lakeel.altla.vision.domain.model.UserDevice;
import com.lakeel.altla.vision.domain.model.UserProfile;

import android.os.Build;

import javax.inject.Inject;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Single;
import rx.schedulers.Schedulers;

public final class SignInWithGoogleUseCase {

    @Inject
    UserProfileRepository userProfileRepository;

    @Inject
    UserDeviceRepository userDeviceRepository;

    @Inject
    public SignInWithGoogleUseCase() {
    }

    public Completable execute(GoogleSignInAccount googleSignInAccount) {
        return Single.just(googleSignInAccount)
                     .flatMap(this::signIn)
                     .flatMap(this::ensureUserProfile)
                     .flatMapCompletable(this::saveUserDevice)
                     .subscribeOn(Schedulers.io());
    }

    private Single<FirebaseUser> signIn(GoogleSignInAccount googleSignInAccount) {
        return Single.create(subscriber -> {
            AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
            Task<AuthResult> task = FirebaseAuth.getInstance().signInWithCredential(credential);

            task.addOnSuccessListener(authResult -> subscriber.onSuccess(authResult.getUser()));
            task.addOnFailureListener(subscriber::onError);
        });
    }

    private Single<FirebaseUser> ensureUserProfile(FirebaseUser firebaseUser) {
        // Check if the user profile exists.
        return Single.create(subscriber -> {
            userProfileRepository.find(firebaseUser.getUid(), userProfile -> {
                // Create the user profile if it does not exist.
                if (userProfile == null) {
                    userProfile = new UserProfile();
                    userProfile.userId = firebaseUser.getUid();
                    userProfile.displayName = firebaseUser.getDisplayName();
                    userProfile.email = firebaseUser.getEmail();
                    if (firebaseUser.getPhotoUrl() != null) {
                        userProfile.photoUri = firebaseUser.getPhotoUrl().toString();
                    }

                    userProfileRepository.save(userProfile);
                }
                subscriber.onSuccess(firebaseUser);
            }, subscriber::onError);
        });
    }

    private Completable saveUserDevice(FirebaseUser firebaseUser) {
        return Completable.create(new Completable.OnSubscribe() {
            @Override
            public void call(CompletableSubscriber subscriber) {
                UserDevice userDevice = new UserDevice();
                userDevice.userId = firebaseUser.getUid();
                userDevice.instanceId = FirebaseInstanceId.getInstance().getId();
                userDevice.creationTime = FirebaseInstanceId.getInstance().getCreationTime();
                userDevice.osName = "android";
                userDevice.osModel = Build.MODEL;
                userDevice.osVersion = Build.VERSION.RELEASE;

                userDeviceRepository.save(userDevice);

                subscriber.onCompleted();
            }
        });
    }
}
