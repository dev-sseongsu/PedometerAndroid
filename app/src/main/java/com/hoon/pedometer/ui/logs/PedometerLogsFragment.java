package com.hoon.pedometer.ui.logs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hoon.pedometer.Injection;
import com.hoon.pedometer.R;
import com.hoon.pedometer.ui.logs.adapter.PedometerLogsAdapter;

import java.util.ArrayList;
import java.util.List;

public class PedometerLogsFragment extends Fragment implements PedometerLogsContract.View {

    private PedometerLogsContract.Presenter mPresenter;

    private RecyclerView mRecyclerView;
    private PedometerLogsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pedometer_logs, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new PedometerLogsAdapter(
                Injection.providePedometerDataSource(getContext()),
                new ArrayList<Long>(0));
        mRecyclerView.setAdapter(mAdapter);

        mPresenter = new PedometerLogsPresenter(Injection.providePedometerDataSource(getContext()));
        mPresenter.bind(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unbind();
    }

    @Override
    public void setDataSet(@NonNull List<Long> dataSet) {
        mAdapter.changeDataSet(dataSet);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
