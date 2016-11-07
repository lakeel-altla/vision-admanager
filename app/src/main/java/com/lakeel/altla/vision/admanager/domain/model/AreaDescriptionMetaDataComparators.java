package com.lakeel.altla.vision.admanager.domain.model;

import java.util.Comparator;

public final class AreaDescriptionMetaDataComparators {

    public static final Comparator<AreaDescriptionMetaData> DATE_COMPARATOR = (d1, d2) -> d1.date.compareTo(d2.date);
}
