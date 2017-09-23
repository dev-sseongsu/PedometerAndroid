package com.hoon.pedometer.ui.state;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hoon.pedometer.data.DailyStep;
import com.hoon.pedometer.data.source.PedometerDataSource;
import com.hoon.pedometer.pedometer.PedometerManager;

import java.util.Date;

public class PedometerStatePresenter implements PedometerStateContract.Presenter,
        PedometerDataSource.OnDailyStepChangeListener,
        PedometerDataSource.OnLocationChangeListener {
    @NonNull
    private final PedometerManager mPedometerManager;
    @NonNull
    private final PedometerDataSource mDataSource;
    private PedometerStateContract.View mView;

    public PedometerStatePresenter(@NonNull PedometerManager pedometerManager,
                                   @NonNull PedometerDataSource dataSource) {
        mPedometerManager = pedometerManager;
        mDataSource = dataSource;
    }

    @Override
    public void bind(@NonNull PedometerStateContract.View view) {
        mView = view;
        if (mPedometerManager.isPedometerStarted()) {
            mView.setPedometerOn(mPedometerManager.startPedometer());
        } else {
            mPedometerManager.stopPedometer();
            mView.setPedometerOn(false);
        }

        mDataSource.registerOnDailyStepChangedListener(this);
        refreshDailyStep();
        mDataSource.registerOnLocationChangeListener(this);
        mView.setCurrentLocation(mDataSource.getLocation());
    }

    @Override
    public void unbind() {
        mView = null;
        mDataSource.unregisterOnDailyStepChangedListener(this);
        mDataSource.unregisterOnLocationChangeListener(this);
    }

    @Override
    public void togglePedometerState() {
        if (mPedometerManager.isPedometerStarted()) {
            mPedometerManager.stopPedometer();
            mView.setPedometerOn(false);
        } else {
            mView.setPedometerOn(mPedometerManager.startPedometer());
        }
    }

    @Override
    public void refreshDailyStep() {
        DailyStep dailyStep = mDataSource.getDailyStepByDate(System.currentTimeMillis());
        if (dailyStep == null) dailyStep = new DailyStep(new Date());
        mView.setDailyStep(dailyStep);
    }

    private boolean isViewActive() {
        return mView != null && mView.isActive();
    }

    @Override
    public void onDailyStepChanged() {
        if (isViewActive()) {
            refreshDailyStep();
        }
    }

    @Override
    public void onLocationChanged(@Nullable String address) {
        if (isViewActive()) {
            mView.setCurrentLocation(address);
        }
    }
}
