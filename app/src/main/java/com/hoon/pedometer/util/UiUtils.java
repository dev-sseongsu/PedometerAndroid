package com.hoon.pedometer.util;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UiUtils {

    private static final DateFormat DF_DATE =
            new SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault());

    public static String formatDate(@NonNull Date date) {
        return DF_DATE.format(date);
    }

}
