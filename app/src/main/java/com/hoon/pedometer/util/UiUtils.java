package com.hoon.pedometer.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.hoon.pedometer.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UiUtils {

    private static final DateFormat DF_DATE =
            new SimpleDateFormat("yyyy-MM-dd (E)", Locale.getDefault());

    public static String formatDate(@NonNull Date date) {
        return DF_DATE.format(date);
    }

    public static void showRequestPermissionRationale(@NonNull final Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.permission_blocked_message)
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent()
                                .setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .addCategory(Intent.CATEGORY_DEFAULT)
                                .setData(Uri.parse("package:" + context.getPackageName()))
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                        Intent.FLAG_ACTIVITY_NO_HISTORY |
                                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS));
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
