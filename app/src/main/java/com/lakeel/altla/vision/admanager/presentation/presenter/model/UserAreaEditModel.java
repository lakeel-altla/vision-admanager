package com.lakeel.altla.vision.admanager.presentation.presenter.model;

import org.parceler.Parcel;

@Parcel
public final class UserAreaEditModel {

    public String userId;

    public String areaId;

    public String name;

    public String placeId;

    public PlaceModel place;

    public int level;

    public long createdAt = -1;

    public long updatedAt = -1;
}
