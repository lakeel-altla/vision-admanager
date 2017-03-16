package com.lakeel.altla.vision.admanager.presentation.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;

public final class DateFormatHelper {

    private DateFormatHelper() {
    }

    @Nullable
    public static String format(@NonNull Context context, long timeMillis) {
        if (-1 < timeMillis) {
            return DateFormat.getDateFormat(context).format(timeMillis) + " " +
                   DateFormat.getTimeFormat(context).format(timeMillis);
        } else {
            return null;
        }
    }
}
