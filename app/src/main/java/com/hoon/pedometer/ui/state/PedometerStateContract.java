package com.hoon.pedometer.ui.state;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hoon.pedometer.data.DailyStep;

public interface PedometerStateContract {
    interface View {

        void setDailyStep(@NonNull DailyStep dailyStep);

        void setCurrentLocation(@Nullable String location);

        void setPedometerOn(boolean on);

        void alertPedometerUnavailable();

        boolean isActive();
    }

    interface Presenter {

        void bind(@NonNull View view);

        void unbind();

        void turnOnPedometer();

        void turnOffPedometer();

        void refreshDailyStep();
    }
}
