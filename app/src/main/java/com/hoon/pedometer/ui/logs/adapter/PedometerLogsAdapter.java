package com.hoon.pedometer.ui.logs.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.hoon.pedometer.data.source.PedometerDataSource;
import com.hoon.pedometer.ui.logs.adapter.viewholder.PedometerLogViewHolder;

import java.util.List;

public class PedometerLogsAdapter extends RecyclerView.Adapter<PedometerLogViewHolder> {

    @NonNull
    private final PedometerDataSource mDataSource;
    @NonNull
    private List<Long> mDailyStepIds;

    public PedometerLogsAdapter(@NonNull PedometerDataSource dataSource,
                                @NonNull List<Long> dailyStepIds) {
        mDataSource = dataSource;
        mDailyStepIds = dailyStepIds;
    }

    @Override
    public PedometerLogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PedometerLogViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(PedometerLogViewHolder holder, int position) {
        holder.bind(mDataSource.getDailyStepById(mDailyStepIds.get(position)));
    }

    @Override
    public int getItemCount() {
        return mDailyStepIds.size();
    }

    public void changeDataSet(@NonNull List<Long> dataSet) {
        this.mDailyStepIds = dataSet;
        notifyDataSetChanged();
    }

}
