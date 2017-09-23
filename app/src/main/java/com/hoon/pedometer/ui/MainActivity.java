package com.hoon.pedometer.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hoon.pedometer.R;
import com.hoon.pedometer.ui.logs.PedometerLogsFragment;
import com.hoon.pedometer.ui.state.PedometerStateFragment;

public class MainActivity extends AppCompatActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(new PedometerPagerAdapter(this, getSupportFragmentManager()));

        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private enum TabSpec {

        PEDOMETER(R.string.pedometer_screen),
        LOGS(R.string.pedometer_logs);

        private final int titleRes;

        TabSpec(int titleRes) {
            this.titleRes = titleRes;
        }

        public int getTitleRes() {
            return titleRes;
        }
    }

    private static class PedometerPagerAdapter extends FragmentStatePagerAdapter {

        @NonNull
        private final Context mContext;
        @NonNull
        private final TabSpec[] mTabSpecs = TabSpec.values();

        PedometerPagerAdapter(@NonNull Context context, @NonNull FragmentManager fm) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (mTabSpecs[position]) {
                case PEDOMETER:
                    return new PedometerStateFragment();
                case LOGS:
                    return new PedometerLogsFragment();
                default:
                    throw new IllegalStateException("unspecified TabSpec");
            }
        }

        @Override
        public int getCount() {
            return mTabSpecs.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mContext.getString(mTabSpecs[position].getTitleRes());
        }
    }
}
