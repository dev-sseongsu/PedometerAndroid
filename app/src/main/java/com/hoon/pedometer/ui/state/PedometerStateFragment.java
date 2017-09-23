package com.hoon.pedometer.ui.state;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hoon.pedometer.Injection;
import com.hoon.pedometer.R;
import com.hoon.pedometer.data.DailyStep;
import com.hoon.pedometer.pedometer.PedometerManager;
import com.hoon.pedometer.ui.BaseFragment;
import com.hoon.pedometer.util.UiUtils;

import java.util.ArrayList;

public class PedometerStateFragment extends BaseFragment
        implements PedometerStateContract.View, View.OnClickListener {

    private PedometerStateContract.Presenter mPresenter;

    private TextView mDate;
    private TextView mStepCount;
    private TextView mDistance;
    private TextView mLocation;
    private Button mBtnStartStop;

    private boolean mIsPedometerOn;

    public PedometerStateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedometer_state, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDate = (TextView) view.findViewById(R.id.date);
        mStepCount = (TextView) view.findViewById(R.id.step_count);
        mDistance = (TextView) view.findViewById(R.id.distance);
        mLocation = (TextView) view.findViewById(R.id.location);
        mBtnStartStop = (Button) view.findViewById(R.id.start_stop_btn);
        mBtnStartStop.setOnClickListener(this);

        mPresenter = new PedometerStatePresenter(
                new PedometerManager(getContext()),
                Injection.providePedometerDataSource(getContext()));
        mPresenter.bind(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unbind();
    }

    @Override
    public void setDailyStep(@NonNull DailyStep dailyStep) {
        mDate.setText(UiUtils.formatDate(dailyStep.getDate()));
        mStepCount.setText(getString(R.string.steps_fmt, dailyStep.getStepCount()));
        mDistance.setText(getString(R.string.km_fmt, dailyStep.getDistanceKm()));
    }

    @Override
    public void setCurrentLocation(@Nullable String location) {
        mLocation.setText(location != null ?
                location : getString(R.string.current_location_error_msg));
    }

    @Override
    public void setPedometerOn(boolean on) {
        mIsPedometerOn = on;
        mBtnStartStop.setText(mIsPedometerOn ? R.string.stop : R.string.start);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_stop_btn:
                if (mIsPedometerOn) {
                    mPresenter.turnOffPedometer();
                } else {
                    // check permission first
                    TedPermission.with(getContext())
                            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                            .setPermissionListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted() {
                                    mPresenter.turnOnPedometer();
                                }

                                @Override
                                public void onPermissionDenied(
                                        ArrayList<String> deniedPermissions) {
                                    UiUtils.showRequestPermissionRationale(getActivity());
                                }
                            })
                            .check();
                }
                break;
        }
    }
}
