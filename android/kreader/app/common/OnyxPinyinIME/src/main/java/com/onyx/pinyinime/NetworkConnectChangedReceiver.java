package com.onyx.pinyinime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by huxiaomao on 2017/1/12.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkConnectChangedReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                NetworkInfo.State state = networkInfo.getState();
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                Log.i(TAG, "isConnected:" + isConnected);
                if (isConnected) {
                    triggerFeedbackUpload(context);
                }
            }
        }
    }

    private void triggerFeedbackUpload(Context context) {
        BroadcastHelper.sendFeedbackUploadBroadcast(context);
    }
}
