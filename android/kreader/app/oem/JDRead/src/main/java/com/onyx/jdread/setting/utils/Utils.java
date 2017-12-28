package com.onyx.jdread.setting.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.onyx.jdread.JDReadApplication;

/**
 * Created by li on 2017/12/25.
 */

public class Utils {
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return activeNetworkInfo.isAvailable();
        }
        return false;
    }

    public static void showMessage(String message) {
        Toast.makeText(JDReadApplication.getInstance(), message, Toast.LENGTH_SHORT).show();
    }

    public static void sendTimeChangeBroadcast() {
        Intent timeChanged = new Intent(Intent.ACTION_TIME_CHANGED);
        JDReadApplication.getInstance().sendBroadcast(timeChanged);
    }
}
