package com.onyx.android.sun.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.common.ManagerActivityUtils;
import com.onyx.android.sun.event.WifiConnectedEvent;
import com.onyx.android.sun.utils.NetworkUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/1/12.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkConnectChangedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) SunApplication.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                EventBus.getDefault().post(new WifiConnectedEvent());
            }
        } else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            if (wifiState == WifiManager.WIFI_STATE_ENABLED){
                int size = NetworkUtil.getConfiguredNetworks(context);
                if (size == 0) {
                    ManagerActivityUtils.startWifiActivity(context);
                }
            }
        }
    }
}
