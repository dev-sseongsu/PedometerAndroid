package com.hoon.pedometer.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class PedometerContract {

    public static final String CONTENT_AUTHORITY = "com.hoon.pedometer";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    static final String PATH_DAILY_STEPS = "daily_steps";

    interface DailyStepsColumns {
        /**
         * Unique date for each record
         */
        String DATE = "card_id";
        String STEP_COUNT = "step_count";
        /**
         * Distance in meter
         */
        String DISTANCE = "distance";
    }

    /**
     * DailySteps are presented on the Explore I/O screen.
     */
    public static class DailySteps implements DailyStepsColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DAILY_STEPS);

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/daily_steps";

        private static final ThreadLocal<DateFormat> DF_DATE = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            }
        };

        @NonNull
        public static String formatDate(long dateMillis) {
            return DF_DATE.get().format(dateMillis);
        }

        @NonNull
        public static Date parseDate(@NonNull String dateString) throws ParseException {
            return DF_DATE.get().parse(dateString);
        }
    }
}
