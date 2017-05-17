package com.onyx.phone.reader.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.onyx.phone.reader.R;

/**
 * Created by suicheng on 2017/2/16.
 */
public class NetworkUtils {

    public static boolean requestWifi(final Context context) {
        if (isWifiConnected(context)) {
            return true;
        } else {
            showWifiDialog(context);
            return false;
        }
    }

    public static void showWifiDialog(final Context context) {
        new MaterialDialog.Builder(context)
                .title(R.string.wifi_dialog_title)
                .content(R.string.wifi_dialog_content)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        context.startActivity(intent);
                    }
                })
                .show();
    }

    public static boolean isActiveNetworkConnected(Context context, int networkType) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected() && info.getType() == networkType) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isWifiConnected(Context context) {
        if (isActiveNetworkConnected(context, ConnectivityManager.TYPE_WIFI)) {
            return true;
        }
        return false;
    }

    public static void toggleWiFi(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean enable = wm.isWifiEnabled();
        wm.setWifiEnabled(!enable);
    }

    public static void enableWifi(Context context, boolean enabled) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wm.setWifiEnabled(enabled);
    }
}
