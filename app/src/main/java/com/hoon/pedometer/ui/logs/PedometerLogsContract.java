package com.hoon.pedometer.ui.logs;

import android.support.annotation.NonNull;

import java.util.List;

public interface PedometerLogsContract {
    interface View {

        void setDataSet(@NonNull List<Long> dataSet);

        boolean isActive();
    }

    interface Presenter {

        void bind(@NonNull View view);

        void unbind();
    }
}
