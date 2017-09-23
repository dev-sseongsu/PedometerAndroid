package com.hoon.pedometer.data;

import java.util.Date;

public class DailyStep {
    private Date date;
    private int stepCount;
    private double distanceMeter;

    public DailyStep(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public double getDistanceMeter() {
        return distanceMeter;
    }

    public void setDistanceMeter(double distanceMeter) {
        this.distanceMeter = distanceMeter;
    }

    public double getDistanceKm() {
        return distanceMeter / 1000;
    }
}
