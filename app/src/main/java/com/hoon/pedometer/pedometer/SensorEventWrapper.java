package com.hoon.pedometer.pedometer;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public class SensorEventWrapper {

    public final SensorEvent event;
    /**
     * {@link SensorEventListener#onSensorChanged(SensorEvent)}
     * 콜백을 통해 이벤트를 전달받은 시점의 timestamp.
     * <p>
     * 기기 별로 {@link SensorEvent#timestamp}의 값이 일관되지 않기 때문에 이를 대체하기 위해 사용.
     */
    public final long onSensorChangedMillis;

    SensorEventWrapper(SensorEvent event, long onSensorChangedMillis) {
        this.event = event;
        this.onSensorChangedMillis = onSensorChangedMillis;
    }
}
