package com.onyx.android.plato.requests.requestTool;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import com.onyx.android.plato.common.ManagerActivityUtils;
import com.onyx.android.plato.utils.NetworkUtil;

import java.util.List;

/**
 * Created by 12 on 2017/5/17.
 */

public class RequestUtils {
    public static int getConfiguredNetworks(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager != null) {
            List<WifiConfiguration> netList = wifiManager.getConfiguredNetworks();
            if (netList == null) {
                return -1;
            }
            return netList.size();
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
