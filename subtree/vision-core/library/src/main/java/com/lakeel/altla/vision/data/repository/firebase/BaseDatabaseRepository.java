package com.lakeel.altla.vision.data.repository.firebase;

import com.google.firebase.database.FirebaseDatabase;

import com.lakeel.altla.vision.ArgumentNullException;

public class BaseDatabaseRepository {

    private final FirebaseDatabase database;

    protected BaseDatabaseRepository(FirebaseDatabase database) {
        if (database == null) throw new ArgumentNullException("database");

        this.database = database;
    }

    protected final FirebaseDatabase getDatabase() {
        return database;
    }
}
