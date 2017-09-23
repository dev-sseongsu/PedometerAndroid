package com.hoon.pedometer.data.source;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hoon.pedometer.data.DailyStep;
import com.hoon.pedometer.provider.PedometerContract.DailySteps;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 로컬에서 보수계 데이터를 관리하는 저장소
 * <p>
 *
 * @see com.hoon.pedometer.provider.PedometerContract
 */
public class PedometerLocalDataSource implements PedometerDataSource {

    private static final String PREF_NAME = "pedometer.pref";
    private static final String PREF_CURRENT_ADDRESS = "PREF_CURRENT_ADDRESS";

    @Nullable
    private volatile static PedometerLocalDataSource sInstance = null;

    @NonNull
    private final ContentResolver mContentResolver;

    @NonNull
    private final SharedPreferences mPreferences;

    @NonNull
    private final ContentObserver mContentObserver =
            new DailyStepsContentObserver(new Handler(Looper.getMainLooper()));

    @NonNull
    private final Set<OnDailyStepChangeListener> mDailyStepChangeListeners = new HashSet<>();
    @NonNull
    private final Set<OnLocationChangeListener> mLocationListeners = new HashSet<>();

    private PedometerLocalDataSource(@NonNull Context context) {
        mContentResolver = context.getContentResolver();
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PedometerLocalDataSource getInstance(@NonNull Context context) {
        if (sInstance == null) {
            synchronized (PedometerLocalDataSource.class) {
                if (sInstance == null) {
                    sInstance = new PedometerLocalDataSource(context);
                }
            }
        }
        return sInstance;
    }

    @Nullable
    @Override
    public DailyStep getDailyStepByDate(long dateMillis) {
        DailyStep dailyStep = null;
        String dateString = DailySteps.formatDate(dateMillis);
        Cursor cursor = mContentResolver.query(DailySteps.CONTENT_URI,
                null, Query.SELECTION_DATE, new String[]{String.valueOf(dateString)}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                dailyStep = getDailyStepFromCursor(cursor);
            }
            cursor.close();
        }
        return dailyStep;
    }

    @Nullable
    @Override
    public DailyStep getDailyStepById(long id) {
        DailyStep dailyStep = null;

        Cursor cursor = mContentResolver.query(DailySteps.CONTENT_URI,
                null, Query.SELECTION_ID, new String[]{String.valueOf(id)}, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                dailyStep = getDailyStepFromCursor(cursor);
            }
            cursor.close();
        }
        return dailyStep;
    }

    @Override
    public void addStepCount(long dateMillis, int addition) {
        String dateString = DailySteps.formatDate(dateMillis);
        String[] selectionArgs = new String[]{String.valueOf(dateString)};
        Cursor cursor = mContentResolver.query(DailySteps.CONTENT_URI,
                Query.PROJECTION_STEP_COUNT,
                Query.SELECTION_DATE, selectionArgs,
                null);

        if (cursor != null) {
            ContentValues values = new ContentValues();
            if (cursor.moveToFirst()) {
                // update existing record
                int newStepCount = cursor.getInt(0) + addition;
                values.put(DailySteps.STEP_COUNT, newStepCount);
                mContentResolver.update(DailySteps.CONTENT_URI, values,
                        Query.SELECTION_DATE, selectionArgs);
            } else {
                // no record exists -- insert
                values.put(DailySteps.DATE, dateString);
                values.put(DailySteps.STEP_COUNT, addition);
                mContentResolver.insert(DailySteps.CONTENT_URI, values);
            }
            cursor.close();
        }
    }

    @Override
    public void addDistance(long dateMillis, double addition) {
        String dateString = DailySteps.formatDate(dateMillis);
        String[] selectionArgs = new String[]{String.valueOf(dateString)};
        Cursor cursor = mContentResolver.query(DailySteps.CONTENT_URI,
                Query.PROJECTION_DISTANCE,
                Query.SELECTION_DATE, selectionArgs,
                null);

        if (cursor != null) {
            ContentValues values = new ContentValues();
            if (cursor.moveToFirst()) {
                // update existing record
                double newDistance = cursor.getDouble(0) + addition;
                values.put(DailySteps.DISTANCE, newDistance);
                mContentResolver.update(DailySteps.CONTENT_URI, values,
                        Query.SELECTION_DATE, selectionArgs);
            } else {
                // no record exists -- insert
                values.put(DailySteps.DATE, dateString);
                values.put(DailySteps.DISTANCE, addition);
                mContentResolver.insert(DailySteps.CONTENT_URI, values);
            }
            cursor.close();
        }
    }

    @Nullable
    @Override
    public String getLocation() {
        return mPreferences.getString(PREF_CURRENT_ADDRESS, null);
    }

    @Override
    public void setLocation(@Nullable String currentAddress) {
        String prevAddress = getLocation();
        if ((currentAddress != prevAddress) &&
                (currentAddress == null || !currentAddress.equals(prevAddress))) {
            // address changed
            mPreferences.edit().putString(PREF_CURRENT_ADDRESS, currentAddress).apply();
            for (OnLocationChangeListener listener : mLocationListeners) {
                listener.onLocationChanged(currentAddress);
            }
        }
    }

    @Override
    public void loadAllDailyStepIds(@NonNull LoadDailyStepIdsCallback callback) {
        Cursor cursor = mContentResolver.query(DailySteps.CONTENT_URI,
                Query.PROJECTION_ID, null, null, Query.DEFAULT_ORDER);

        if (cursor != null) {
            List<Long> ids = new ArrayList<>(cursor.getCount());

            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                ids.add(cursor.getLong(0));
            }

            cursor.close();
            callback.onLoadDailyStepIdsSuccess(ids);
        } else {
            callback.onDailyStepStepIdsFailure();
        }
    }

    @Override
    public void registerOnDailyStepChangedListener(@NonNull OnDailyStepChangeListener l) {
        if (mDailyStepChangeListeners.isEmpty()) {
            mContentResolver.registerContentObserver(
                    DailySteps.CONTENT_URI, true, mContentObserver);
        }
        mDailyStepChangeListeners.add(l);
    }

    @Override
    public void unregisterOnDailyStepChangedListener(@NonNull OnDailyStepChangeListener l) {
        mDailyStepChangeListeners.remove(l);
        if (mDailyStepChangeListeners.isEmpty()) {
            mContentResolver.unregisterContentObserver(mContentObserver);
        }
    }

    @Override
    public void registerOnLocationChangeListener(@NonNull OnLocationChangeListener l) {
        mLocationListeners.add(l);
    }

    @Override
    public void unregisterOnLocationChangeListener(@NonNull OnLocationChangeListener l) {
        mLocationListeners.remove(l);
    }

    @Nullable
    private DailyStep getDailyStepFromCursor(@NonNull Cursor cursor) {
        try {
            DailyStep dailyStep = new DailyStep(DailySteps.parseDate(
                    cursor.getString(cursor.getColumnIndex(DailySteps.DATE))));
            dailyStep.setStepCount(
                    cursor.getInt(cursor.getColumnIndex(DailySteps.STEP_COUNT)));
            dailyStep.setDistanceMeter(
                    cursor.getInt(cursor.getColumnIndex(DailySteps.DISTANCE)));

            return dailyStep;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private interface Query {
        String[] PROJECTION_ID = new String[]{DailySteps._ID};
        String[] PROJECTION_STEP_COUNT = new String[]{DailySteps.STEP_COUNT};
        String[] PROJECTION_DISTANCE = new String[]{DailySteps.DISTANCE};
        String SELECTION_ID = DailySteps._ID + "=?";
        String SELECTION_DATE = DailySteps.DATE + "=?";
        String DEFAULT_ORDER = DailySteps.DATE + " DESC";
    }

    private class DailyStepsContentObserver extends ContentObserver {

        DailyStepsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            for (OnDailyStepChangeListener listener : mDailyStepChangeListeners) {
                listener.onDailyStepChanged();
            }
        }
    }
}
