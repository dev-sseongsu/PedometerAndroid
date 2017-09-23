package com.hoon.pedometer.ui.logs.adapter.viewholder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hoon.pedometer.R;
import com.hoon.pedometer.data.DailyStep;
import com.hoon.pedometer.util.UiUtils;

public class PedometerLogViewHolder extends RecyclerView.ViewHolder {

    private final TextView mDate;
    private final TextView mStepCount;
    private final TextView mDistance;

    private PedometerLogViewHolder(View itemView) {
        super(itemView);
        mDate = (TextView) itemView.findViewById(R.id.date);
        mStepCount = (TextView) itemView.findViewById(R.id.step_count);
        mDistance = (TextView) itemView.findViewById(R.id.distance);
    }

    public static PedometerLogViewHolder newInstance(@NonNull ViewGroup parent) {
        return new PedometerLogViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cell_pedometer_log, parent, false));
    }

    public void bind(@Nullable DailyStep dailyStep) {
        if (dailyStep != null) {
            Context context = mDate.getContext();
            mDate.setText(UiUtils.formatDate(dailyStep.getDate()));
            mStepCount.setText(context.getString(R.string.steps_fmt, dailyStep.getStepCount()));
            mDistance.setText(context.getString(R.string.km_fmt, dailyStep.getDistanceKm()));
        } else {
            // clear values
            mDate.setText("");
            mStepCount.setText("");
            mDistance.setText("");
        }
    }
}
