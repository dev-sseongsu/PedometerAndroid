package com.hoon.pedometer.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hoon.pedometer.data.DailyStep;

import java.util.Date;
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

    void loadAllDailyStepIds(@NonNull LoadDailyStepIdsCallback callback);

    void registerOnDailyStepChangedListener(@NonNull OnDailyStepChangedListener l);

    void unregisterOnDailyStepChangedListener(@NonNull OnDailyStepChangedListener l);

    interface LoadDailyStepIdsCallback {
        void onLoadDailyStepIdsSuccess(@NonNull List<Long> ids);

        void onDailyStepStepIdsFailure();
    }

    interface OnDailyStepChangedListener {
        void onDailyStepChanged();
    }

}

