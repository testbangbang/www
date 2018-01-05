package com.onyx.edu.homework.request;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.onyx.android.sdk.common.receiver.NetworkConnectChangedReceiver;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.NetworkUtil;

/**
 * Created by lxm on 2018/1/5.
 */

public class CheckWifiRequest extends BaseDataRequest {

    private NetworkConnectChangedReceiver networkConnectChangedReceiver;
    private boolean isConnected;

    @Override
    public void execute(DataManager dataManager) throws Exception {
        if (NetworkUtil.isWiFiConnected(getContext())) {
            setConnected(true);
            getCallback().done(CheckWifiRequest.this, null);
            return;
        }
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver(new NetworkConnectChangedReceiver.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean connected, int networkType) {
                setConnected(connected);
                getCallback().done(CheckWifiRequest.this, null);
                if (connected) {
                    unregisterReceiver(getContext());
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkConnectChangedReceiver, filter);
        NetworkUtil.enableWiFi(getContext(), true);
    }

    private void unregisterReceiver(Context context) {
        if (networkConnectChangedReceiver == null) {
            return;
        }
        context.unregisterReceiver(networkConnectChangedReceiver);
        networkConnectChangedReceiver = null;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void setConnected(boolean connected) {
        isConnected = connected;
    }
}
