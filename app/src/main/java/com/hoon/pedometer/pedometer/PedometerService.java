package com.hoon.pedometer.pedometer;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;

import com.hoon.pedometer.Injection;
import com.hoon.pedometer.data.source.PedometerDataSource;
import com.hoon.pedometer.pedometer.processor.SensorEventProcessor;
import com.hoon.pedometer.pedometer.processor.StepCounterProcessor;

/**
 * 걸음 수 측정 서비스
 */
public class PedometerService extends Service implements SensorEventListener {

    private SensorManager mSensorManager;
    private SensorEventProcessor mProcessor;
    private PedometerDataSource mDataSource;

    private volatile Looper mServiceLooper;
    private volatile PedometerHandler mPedometerHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        PedometerManager manager = new PedometerManager(this);
        if (manager.isPedometerAvailable()) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            mProcessor = provideProcessor();
            if (mProcessor != null) {
                Sensor sensor = mSensorManager.getDefaultSensor(mProcessor.getSensorType());
                if (sensor != null) {
                    HandlerThread thread = new HandlerThread(getClass().getSimpleName());
                    thread.start();
                    mServiceLooper = thread.getLooper();
                    mPedometerHandler = new PedometerHandler(mServiceLooper);

                    mDataSource = Injection.providePedometerDataSource(getApplicationContext());

                    mSensorManager.registerListener(this, sensor,
                            SensorManager.SENSOR_DELAY_FASTEST);
                    return; // 보수계 실행
                }
            }
        }
        manager.stopPedometer();
        stopSelf(); // 보수계 실행 불가
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mSensorManager != null) mSensorManager.unregisterListener(this);
        if (mServiceLooper != null) mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long onSensorChangedMillis = System.currentTimeMillis();
        Message msg = mPedometerHandler.obtainMessage();
        msg.obj = new SensorEventWrapper(event, onSensorChangedMillis);
        mPedometerHandler.sendMessage(msg); // 백그라운드에서 이벤트 처리
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * 현재 기기 및 실행 환경에 적합한 프로세서를 제공
     *
     * @return 프로세서
     */
    @Nullable
    private SensorEventProcessor provideProcessor() {
        // TODO 버전 별 분기
        return new StepCounterProcessor();
    }

    /**
     * 센서 이벤트 처리를 백그라운드에서 수행하기 위한 클래스
     */
    private class PedometerHandler extends Handler {
        PedometerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            SensorEventWrapper eventWrapper = (SensorEventWrapper) msg.obj;
            int stepCount = mProcessor.calcStepCount(eventWrapper);
            if (stepCount > 0) {
                mDataSource.addStepCount(eventWrapper.onSensorChangedMillis, stepCount);
            }
        }
    }
}
