package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import org.parceler.Parcel;

@Parcel
public final class UserSceneEditModel {

    public String userId;

    public String sceneId;

    public String name;

    public String areaId;

    public String areaName;

    public long createdAt = -1;

    public long updatedAt = -1;
}
