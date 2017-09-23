package com.hoon.pedometer.pedometer.processor;

import android.support.annotation.NonNull;

import com.hoon.pedometer.pedometer.SensorEventWrapper;

public interface SensorEventProcessor {
    /**
     * 걸음 수 측정에 사용 할 {@link android.hardware.Sensor} 타입을 반환
     *
     * @return 센서 타입
     */
    int getSensorType();

    /**
     * 센서 이벤트를 통해 걸음 수를 계산
     *
     * @param eventWrapper 센서 이벤트
     * @return 계산된 걸음 수
     */
    int calcStepCount(@NonNull SensorEventWrapper eventWrapper);
}
