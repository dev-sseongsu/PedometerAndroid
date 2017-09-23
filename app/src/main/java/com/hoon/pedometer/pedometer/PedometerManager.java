package com.hoon.pedometer.pedometer;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * 보수계 상태를 관리하기 위한 클래스
 */
public class PedometerManager {

    private static final String PREF_NAME = "pedometer.pref";
    private static final String PREF_IS_PEDOMETER_STARTED = "PREF_IS_PEDOMETER_STARTED";

    private final Context mAppContext;
    private final SharedPreferences mPreferences;
    private final boolean mIsPedometerAvilable;

    public PedometerManager(@NonNull Context context) {
        mAppContext = context.getApplicationContext();
        mPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        mIsPedometerAvilable =
                isStepCounterAvailable(context) || isAccelerometerAvailable(context);
    }

    public static boolean isStepCounterAvailable(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PackageManager pm = context.getPackageManager();
            SensorManager sm =
                    (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER)
                    && !sm.getSensorList(Sensor.TYPE_STEP_COUNTER).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAccelerometerAvailable(@NonNull Context context) {
        PackageManager pm = context.getPackageManager();
        SensorManager sm =
                (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)
                && !sm.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean isPedometerAvailable() {
        return mIsPedometerAvilable;
    }

    public boolean isPedometerStarted() {
        return mPreferences.getBoolean(PREF_IS_PEDOMETER_STARTED, false);
    }

    public void startPedometer() {
        mPreferences.edit().putBoolean(PREF_IS_PEDOMETER_STARTED, true).apply();
        mAppContext.startService(new Intent(mAppContext, PedometerService.class));
    }

    public void stopPedometer() {
        mPreferences.edit().putBoolean(PREF_IS_PEDOMETER_STARTED, false).apply();
        mAppContext.stopService(new Intent(mAppContext, PedometerService.class));
    }

}
