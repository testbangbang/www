package com.onyx.android.sdk.ui.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

import com.onyx.android.sdk.ui.R;

/**
 * @author Simon
 */
public class NetworkHelper {

    public static boolean requestWifi(final Context context) {
        if (isWifiConnected(context)) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.wifi_dialog_title).setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.putExtra("closeWindowOnConnected", true);
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .setMessage(R.string.wifi_dialog_content)
                    .show();
            return false;
        }
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
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

    public static boolean isWifiEnable(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return wm.isWifiEnabled();
    }

    public static boolean isConnectingOrConnected(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
