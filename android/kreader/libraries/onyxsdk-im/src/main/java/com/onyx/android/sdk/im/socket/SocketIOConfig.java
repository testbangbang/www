package com.onyx.android.sdk.im.socket;

/**
 * Created by ming on 2017/7/14.
 */

public class SocketIOConfig {

    private int reconnectCount = 5;

    private int intervalAfterConnectFailed = 2000;

    public int getReconnectCount() {
        return reconnectCount;
    }

    public void setReconnectCount(int reconnectCount) {
        this.reconnectCount = reconnectCount;
    }

    public int getIntervalAfterConnectFailed() {
        return intervalAfterConnectFailed;
    }

    public void setIntervalAfterConnectFailed(int intervalAfterConnectFailed) {
        this.intervalAfterConnectFailed = intervalAfterConnectFailed;
    }

    public boolean canReconnect(int reconnectCount) {
        return reconnectCount < this.reconnectCount;
    }
}
