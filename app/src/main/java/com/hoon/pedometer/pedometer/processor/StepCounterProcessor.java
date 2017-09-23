package com.hoon.pedometer.pedometer.processor;


import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.hoon.pedometer.pedometer.SensorEventWrapper;

/**
 * {@link Sensor#TYPE_STEP_COUNTER} 센서를 사용하여 걸음 수를 계산하는 클래스
 */
public class StepCounterProcessor implements SensorEventProcessor {

    private static final String PREF_NAME = "step_counter.pref";
    private static final String PREF_LATEST_COUNTER_COUNT = "PREF_LATEST_COUNTER_COUNT";
    @NonNull
    private final SharedPreferences mPreferences;

    public StepCounterProcessor(@NonNull Context context) {
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int getSensorType() {
        return Sensor.TYPE_STEP_COUNTER;
    }

    @Override
    public int calcStepCount(@NonNull SensorEventWrapper eventWrapper) {
        int stepCount = 0;
        SensorEvent event = eventWrapper.event;
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            int latestCounterCount = getLatestCounterCount();
            int newCounterCount = Math.round(event.values[0]);
            if (latestCounterCount > 0) {
                stepCount = newCounterCount - latestCounterCount;
            }
            setLatestCounterCount(newCounterCount);
        }
        return stepCount;
    }

    private int getLatestCounterCount() {
        return mPreferences.getInt(PREF_LATEST_COUNTER_COUNT, 0);
    }

    private void setLatestCounterCount(int count) {
        mPreferences.edit().putInt(PREF_LATEST_COUNTER_COUNT, count).apply();
    }
}
