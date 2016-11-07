package com.lakeel.altla.vision.admanager.data.repository;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class FirebaseMetaData {

    public String uuid;

    public String name;

    public long date;

    public List<Double> transformationPosition;

    public List<Double> transformationRotation;
}
