package com.onyx.edu.reader.ui.events;

import android.content.Context;

/**
 * Created by ming on 2017/2/8.
 */

public class NetworkChangedEvent {

    private Context context;
    private boolean connected;
    private int networkType;

    public NetworkChangedEvent(Context context, boolean connected, int networkType) {
        this.connected = connected;
        this.context = context;
        this.networkType = networkType;
    }

    public static NetworkChangedEvent create(Context context, boolean connected, int networkType) {
        return new NetworkChangedEvent(context, connected, networkType);
    }

    public boolean isConnected() {
        return connected;
    }

    public Context getContext() {
        return context;
    }

    public int getNetworkType() {
        return networkType;
    }
}
