package com.onyx.kreader.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 2017/2/8.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkConnectChangedRe";

    private ReaderDataHolder readerDataHolder;

    public NetworkConnectChangedReceiver() {

    }

    public NetworkConnectChangedReceiver(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            ConnectivityManager manager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                readerDataHolder.onNetworkChanged(activeNetwork.isConnected(), activeNetwork.getType());
            }

        }
    }
}
