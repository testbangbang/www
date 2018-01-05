package com.onyx.edu.homework.action;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.onyx.android.sdk.common.receiver.NetworkConnectChangedReceiver;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.utils.NetworkUtil;
import com.onyx.edu.homework.base.BaseAction;
import com.onyx.edu.homework.request.CheckWifiRequest;

/**
 * Created by lxm on 2018/1/3.
 */

public class CheckWifiAction extends BaseAction {

    private volatile boolean isConnected;

    @Override
    public void execute(final Context context, final BaseCallback baseCallback) {
        final CheckWifiRequest wifiRequest = new CheckWifiRequest();
        wifiRequest.setContext(context.getApplicationContext());
        getDataManager().submit(context, wifiRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                isConnected = wifiRequest.isConnected();
                BaseCallback.invoke(baseCallback, request, null);
            }
        });
    }

    public boolean isConnected() {
        return isConnected;
    }
}
