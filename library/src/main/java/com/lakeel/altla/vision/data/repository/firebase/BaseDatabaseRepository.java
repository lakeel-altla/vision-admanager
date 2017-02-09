package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.FirebaseDatabase;

import android.support.annotation.NonNull;

public class BaseDatabaseRepository {

    private final FirebaseDatabase database;

    protected BaseDatabaseRepository(@NonNull FirebaseDatabase database) {
        this.database = database;
    }

    @NonNull
    protected final FirebaseDatabase getDatabase() {
        return database;
    }
}
