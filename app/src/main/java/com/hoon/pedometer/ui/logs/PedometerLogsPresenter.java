package com.hoon.pedometer.ui.logs;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hoon.pedometer.data.source.PedometerDataSource;

import java.util.List;

public class PedometerLogsPresenter implements PedometerLogsContract.Presenter,
        PedometerDataSource.LoadDailyStepIdsCallback,
        PedometerDataSource.OnDailyStepChangedListener {

    @NonNull
    private final PedometerDataSource mDataSource;
    private PedometerLogsContract.View mView;

    public PedometerLogsPresenter(@NonNull PedometerDataSource dataSource) {
        mDataSource = dataSource;
    }

    @Override
    public void bind(@NonNull PedometerLogsContract.View view) {
        mView = view;
        mDataSource.registerOnDailyStepChangedListener(this);
        mDataSource.loadAllDailyStepIds(this);
    }

    @Override
    public void unbind() {
        mView = null;
        mDataSource.unregisterOnDailyStepChangedListener(this);
    }

    private boolean isViewActive() {
        return mView != null && mView.isActive();
    }

    @Override
    public void onLoadDailyStepIdsSuccess(@NonNull List<Long> ids) {
        if (isViewActive()) {
            mView.setDataSet(ids);
        }
    }

    @Override
    public void onDailyStepStepIdsFailure() {

    }

    @Override
    public void onDailyStepChanged() {
        mDataSource.loadAllDailyStepIds(this);
    }
}
