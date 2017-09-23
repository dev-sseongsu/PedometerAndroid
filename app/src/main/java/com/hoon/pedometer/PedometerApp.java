package com.hoon.pedometer;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class PedometerApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
