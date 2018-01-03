package com.onyx.edu.homework.action;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.onyx.android.sdk.common.receiver.NetworkConnectChangedReceiver;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.homework.base.BaseAction;

/**
 * Created by lxm on 2018/1/3.
 */

public class WifiConnectAction extends BaseAction {

    private NetworkConnectChangedReceiver networkConnectChangedReceiver;

    @Override
    public void execute(final Context context, final BaseCallback baseCallback) {
        networkConnectChangedReceiver = new NetworkConnectChangedReceiver(new NetworkConnectChangedReceiver.NetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean connected, int networkType) {
                if (connected) {
                    unregisterReceiver(context);
                    BaseCallback.invoke(baseCallback, null, null);
                }
            }
        });
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkConnectChangedReceiver, filter);
        NetworkUtil.enableWiFi(context, true);
    }

    private void unregisterReceiver(Context context) {
        if (networkConnectChangedReceiver == null) {
            return;
        }
        context.unregisterReceiver(networkConnectChangedReceiver);
        networkConnectChangedReceiver = null;
    }
}
