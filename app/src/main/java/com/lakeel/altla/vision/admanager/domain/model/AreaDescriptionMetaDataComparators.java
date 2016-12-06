package com.lakeel.altla.vision.admanager.domain.model;

import java.util.Comparator;

public final class AreaDescriptionMetadataComparators {

    public static final Comparator<AreaDescriptionMetadata> DATE_COMPARATOR = (d1, d2) -> d1.date.compareTo(d2.date);
}
