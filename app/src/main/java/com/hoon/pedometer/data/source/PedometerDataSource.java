package com.hoon.pedometer.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hoon.pedometer.data.DailyStep;

import java.util.List;

/**
 * 보수계 기록 저장소
 */
public interface PedometerDataSource {

    @Nullable
    DailyStep getDailyStepByDate(long dateMillis);

    @Nullable
    DailyStep getDailyStepById(long rowId);

    void addStepCount(long dateMillis, int stepCount);

    void addDistance(long dateMillis, double addition);

    String getLocation();

    void setLocation(@Nullable String currentAddress);

    void loadAllDailyStepIds(@NonNull LoadDailyStepIdsCallback callback);

    void registerOnDailyStepChangedListener(@NonNull OnDailyStepChangeListener l);

    void unregisterOnDailyStepChangedListener(@NonNull OnDailyStepChangeListener l);

    void registerOnLocationChangeListener(@NonNull OnLocationChangeListener l);

    void unregisterOnLocationChangeListener(@NonNull OnLocationChangeListener l);

    interface LoadDailyStepIdsCallback {
        void onLoadDailyStepIdsSuccess(@NonNull List<Long> ids);

        void onDailyStepStepIdsFailure();
    }

    interface OnDailyStepChangeListener {
        void onDailyStepChanged();
    }

    interface OnLocationChangeListener {
        void onLocationChanged(@Nullable String address);
    }

}

