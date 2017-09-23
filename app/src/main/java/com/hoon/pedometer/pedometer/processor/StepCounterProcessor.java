package com.hoon.pedometer.pedometer.processor;


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

    private int mLastCounterCount;

    public StepCounterProcessor() {
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
            int newCounterCount = Math.round(event.values[0]);
            if (mLastCounterCount > 0) {
                stepCount = newCounterCount - mLastCounterCount;
            }
            mLastCounterCount = newCounterCount;
        }
        return stepCount;
    }
}
