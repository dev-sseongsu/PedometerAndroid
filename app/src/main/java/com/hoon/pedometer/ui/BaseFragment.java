package com.hoon.pedometer.ui;

import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.widget.Toast;

public class BaseFragment extends Fragment {
    private Toast mToast;

    public void showToast(@StringRes int resId) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getContext(), resId, Toast.LENGTH_LONG);
        mToast.show();
    }
}
