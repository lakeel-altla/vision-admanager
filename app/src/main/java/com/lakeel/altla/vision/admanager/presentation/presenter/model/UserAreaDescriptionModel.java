package com.lakeel.altla.vision.admanager.presentation.presenter.model;

public final class UserAreaDescriptionModel {

    public String areaDescriptionId;

    public long createdAt;

    public boolean fileUploaded;

    public String name;

    public String areaId;

    public String areaName;

    public ImportStatus importStatus = ImportStatus.UNKNOWN;

    public boolean fileCached;
}
