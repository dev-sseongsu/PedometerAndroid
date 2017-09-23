package com.hoon.pedometer;


import android.content.Context;
import android.support.annotation.NonNull;

import com.hoon.pedometer.data.source.PedometerDataSource;
import com.hoon.pedometer.data.source.PedometerLocalDataSource;

public class Injection {

    @NonNull
    public static PedometerDataSource providePedometerDataSource(@NonNull Context context) {
        return PedometerLocalDataSource.getInstance(context);
    }

}
