package com.onyx.android.sdk.data.model.v2;

/**
 * Created by suicheng on 2017/8/18.
 */

public class BindServer {
    public Server server;
    public DeviceBind device;

    public BindServer(Server server, DeviceBind deviceBind) {
        this.server = server;
        this.device = deviceBind;
    }
}
