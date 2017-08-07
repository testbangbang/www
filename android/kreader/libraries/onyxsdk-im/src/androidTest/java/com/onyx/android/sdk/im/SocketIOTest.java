package com.onyx.android.sdk.im;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * Created by john on 15/7/2017.
 */

public class SocketIOTest  extends ApplicationTestCase<Application> {

    public SocketIOTest() {
        super(Application.class);
    }

    public void testConnect() {
        final IMConfig config = new IMConfig();
        config.setServerUri("http://localhost:3000");
        IMManager.getInstance().init(config);
        IMManager.getInstance()
                .startPushService(getContext())
                .startSocketService(getContext());

    }
}
