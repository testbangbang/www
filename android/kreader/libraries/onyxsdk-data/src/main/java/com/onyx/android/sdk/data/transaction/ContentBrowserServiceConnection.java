package com.onyx.android.sdk.data.transaction;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.onyx.android.sdk.rx.RxRequest;

/**
 * Created by zhuzeng on 7/5/14.
 */
public class ContentBrowserServiceConnection implements ServiceConnection {
    private volatile boolean connected = false;
    private volatile IBinder remoteService;
    public ContentBrowserServiceConnection() {
        super();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        connected = true;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        connected = true;
        remoteService = service;
    }

    public IBinder getRemoteService() {
        return remoteService;
    }

    public void waitUntilConnected(final RxRequest request) throws InterruptedException {
        while (!connected && !request.getAbort()) {
            Thread.sleep(100);
        }
    }

}
