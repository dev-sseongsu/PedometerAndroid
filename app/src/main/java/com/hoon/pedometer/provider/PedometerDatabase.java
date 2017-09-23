package com.hoon.pedometer.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.hoon.pedometer.provider.PedometerContract.DailyStepsColumns;

class PedometerDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pedometer.db";
    private static final int DATABASE_VERSION = 1;

    PedometerDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.DAILY_STEPS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DailyStepsColumns.DATE + " TEXT NOT NULL,"
                + DailyStepsColumns.STEP_COUNT + " INTEGER DEFAULT 0,"
                + DailyStepsColumns.DISTANCE + " REAL DEFAULT 0,"
                + "UNIQUE (" + DailyStepsColumns.DATE + ") ON CONFLICT REPLACE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    interface Tables {
        String DAILY_STEPS = "daily_steps";
    }
}
