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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hoon.pedometer.Injection;
import com.hoon.pedometer.data.source.PedometerDataSource;
import com.hoon.pedometer.api.MapApi;
import com.hoon.pedometer.api.NaverRestApiHelper;
import com.hoon.pedometer.api.response.ReverseGeocodeResponse;
import com.hoon.pedometer.pedometer.processor.AccelerometerProcessor;
import com.hoon.pedometer.pedometer.processor.SensorEventProcessor;
import com.hoon.pedometer.pedometer.processor.StepCounterProcessor;
import com.nhn.android.maps.NMapLocationManager;
import com.nhn.android.maps.maplib.NGeoPoint;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 걸음 수 측정 서비스
 */
public class PedometerService extends Service implements SensorEventListener,
        NMapLocationManager.OnLocationChangeListener {

    private SensorManager mSensorManager;
    private SensorEventProcessor mProcessor;
    private PedometerDataSource mDataSource;

    private volatile SensorHandler mSensorHandler;
    private volatile LocationHandler mLocationHandler;

    private NMapLocationManager mLocationManager;


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

                    mSensorHandler = new SensorHandler(createBackgroundLooper("pedometer"));
                    mLocationHandler = new LocationHandler(createBackgroundLooper("location"));

                    mDataSource = Injection.providePedometerDataSource(getApplicationContext());

                    mSensorManager.registerListener(this, sensor,
                            SensorManager.SENSOR_DELAY_FASTEST);

                    mLocationManager = new NMapLocationManager(this);
                    mLocationManager.enableMyLocation(true);
                    mLocationManager.setOnLocationChangeListener(this);
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
        if (mSensorHandler != null) mSensorHandler.getLooper().quit();
        if (mLocationHandler != null) mLocationHandler.getLooper().quit();
        if (mLocationManager != null) {
            mLocationManager.removeOnLocationChangeListener(this);
            mLocationManager.enableMyLocation(false);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Looper createBackgroundLooper(@NonNull String name) {
        HandlerThread thread = new HandlerThread(name);
        thread.start();
        return thread.getLooper();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long onSensorChangedMillis = System.currentTimeMillis();
        Message msg = mSensorHandler.obtainMessage();
        msg.obj = new SensorEventWrapper(event, onSensorChangedMillis);
        mSensorHandler.sendMessage(msg); // 백그라운드에서 이벤트 처리
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

    @Override
    public boolean onLocationChanged(
            NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
        Message msg = mLocationHandler.obtainMessage();
        msg.obj = nGeoPoint;
        mLocationHandler.sendMessage(msg); // 백그라운드에서 이벤트 처리
        return true;
    }

    @Override
    public void onLocationUpdateTimeout(NMapLocationManager nMapLocationManager) {

    }

    @Override
    public void onLocationUnavailableArea(
            NMapLocationManager nMapLocationManager, NGeoPoint nGeoPoint) {
    }

    /**
     * 센서 이벤트 처리를 백그라운드에서 수행하기 위한 클래스
     */
    private class SensorHandler extends Handler {
        SensorHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            SensorEventWrapper eventWrapper = (SensorEventWrapper) msg.obj;
            int stepCount = mProcessor.calcStepCount(eventWrapper);
            if (stepCount > 0)
                if (stepCount > 0) {
                    mDataSource.addStepCount(eventWrapper.onSensorChangedMillis, stepCount);
                }
        }
    }

    /**
     * 위치 변경 이벤트 처리를 백그라운드에서 수행하기 위한 클래스
     */
    private class LocationHandler extends Handler {
        @NonNull
        private final MapApi mMapApi = NaverRestApiHelper.getRetrofit().create(MapApi.class);
        private NGeoPoint mLastGeoPoint;

        LocationHandler(Looper looper) {
            super(looper);
        }

        private void addDistance(@NonNull NGeoPoint currentGeoPoint) {
            if (mLastGeoPoint != null) {
                mDataSource.addDistance(System.currentTimeMillis(),
                        NGeoPoint.getDistance(mLastGeoPoint, currentGeoPoint));
            }
            mLastGeoPoint = currentGeoPoint;
        }

        private void setCurrentAddress(@NonNull NGeoPoint geoPoint) {
            mMapApi.reverseGeocode(geoPoint.getLongitude() + "," + geoPoint.getLatitude())
                    .enqueue(new retrofit2.Callback<ReverseGeocodeResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<ReverseGeocodeResponse> call,
                                               @NonNull Response<ReverseGeocodeResponse> response) {
                            ReverseGeocodeResponse resp = response.body();
                            if (resp != null) {
                                mDataSource.setLocation(resp.getAddress());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ReverseGeocodeResponse> call,
                                              @NonNull Throwable t) {
                            mDataSource.setLocation(null);
                        }
                    });
        }

        @Override
        public void handleMessage(Message msg) {
            NGeoPoint currentGeoPoint = (NGeoPoint) msg.obj;
            addDistance(currentGeoPoint);
            setCurrentAddress(currentGeoPoint);
        }
    }
}
