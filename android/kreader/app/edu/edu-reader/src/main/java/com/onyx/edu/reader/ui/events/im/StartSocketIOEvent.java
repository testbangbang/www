package com.onyx.edu.reader.ui.events.im;

import com.onyx.android.sdk.im.IMConfig;

/**
 * Created by lxm on 2017/8/10.
 */

public class StartSocketIOEvent {

    private IMConfig config;

    public StartSocketIOEvent(IMConfig config) {
        this.config = config;
    }

    public IMConfig getConfig() {
        return config;
    }

    public static StartSocketIOEvent create(IMConfig config) {
        return new StartSocketIOEvent(config);
    }
}
