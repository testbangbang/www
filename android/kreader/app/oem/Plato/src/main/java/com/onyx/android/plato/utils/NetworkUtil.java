package com.onyx.android.plato.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.onyx.android.plato.common.ManagerActivityUtils;

import java.util.List;

/**
 * Created by hehai on 17-10-9.
 */

public class NetworkUtil {
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConfiguredNetworks(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            List<WifiConfiguration> netlist = wifiManager.getConfiguredNetworks();
            if (netlist == null) {
                return -1;
            }
            return netlist.size();
        }
        return 0;
    }

    public static void toggleWiFi(Context context, boolean enabled) {
        if (!NetworkUtil.isNetworkConnected(context) && getConfiguredNetworks(context) == 0) {
            ManagerActivityUtils.startWifiActivity(context);
        } else {
            WifiManager wm = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            wm.setWifiEnabled(enabled);
        }
    }
}
