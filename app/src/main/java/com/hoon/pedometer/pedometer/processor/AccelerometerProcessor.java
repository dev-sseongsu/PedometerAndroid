package com.hoon.pedometer.pedometer.processor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.hoon.pedometer.pedometer.SensorEventWrapper;

/**
 * {@link Sensor#TYPE_ACCELEROMETER} 센서를 사용하여 걸음 수를 계산하는 클래스.
 * <p>
 *  다음 링크의 코드를 참조함
 *  <a href="https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/StepDetector.java">StepDetector.java</a>
 */
public class AccelerometerProcessor implements SensorEventProcessor {
    private float mLimit = 10;
    private float mLastValues[] = new float[3 * 2];
    private float mScale;
    private float mYOffset;

    private float mLastDirections[] = new float[3 * 2];
    private float mLastExtremes[][] = {new float[3 * 2], new float[3 * 2]};
    private float mLastDiff[] = new float[3 * 2];
    private int mLastMatch = -1;

    public AccelerometerProcessor() {
        int h = 480; // TODO: remove this constant
        mYOffset = h * 0.5f;
        mScale = -(h * 0.5f * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    @Override
    public int getSensorType() {
        return Sensor.TYPE_ACCELEROMETER;
    }

    @Override
    public int calcStepCount(@NonNull SensorEventWrapper eventWrapper) {
        synchronized (this) {
            int step = 0;
            SensorEvent event = eventWrapper.event;
            float vSum = 0;
            for (int i = 0; i < 3; i++) {
                final float v = mYOffset + event.values[i] * mScale;
                vSum += v;
            }
            int k = 0;
            float v = vSum / 3;

            float direction = (v > mLastValues[k] ? 1 : (v < mLastValues[k] ? -1 : 0));
            if (direction == -mLastDirections[k]) {
                // Direction changed
                int extType = (direction > 0 ? 0 : 1); // minumum or maximum?
                mLastExtremes[extType][k] = mLastValues[k];
                float diff = Math.abs(mLastExtremes[extType][k] - mLastExtremes[1 - extType][k]);

                if (diff > mLimit) {

                    boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff[k] * 2 / 3);
                    boolean isPreviousLargeEnough = mLastDiff[k] > (diff / 3);
                    boolean isNotContra = (mLastMatch != 1 - extType);

                    if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                        step = 1;
                        mLastMatch = extType;
                    } else {
                        mLastMatch = -1;
                    }
                }
                mLastDiff[k] = diff;
            }
            mLastDirections[k] = direction;
            mLastValues[k] = v;
            return step;
        }
    }
}
