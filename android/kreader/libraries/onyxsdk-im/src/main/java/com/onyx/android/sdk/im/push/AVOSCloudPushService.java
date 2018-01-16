package com.onyx.android.sdk.im.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.PushService;
import com.onyx.android.sdk.im.IMConfig;

/**
 * Created by ming on 2017/7/13.
 */

public class AVOSCloudPushService implements BasePushService {

    @Override
    public void init(Context context, IMConfig config) {
        if (config == null) {
            return;
        }
        LeanCloudManager.initialize(context.getApplicationContext(), config.getApplicationId(),
                config.getClientKey());
        Class<? extends Activity> clazz = config.getPushCallbackActivity();
        if (clazz != null) {
            PushService.setDefaultPushCallback(context.getApplicationContext(), clazz);
        }
    }

    @Override
    public void start(Context context) {
    }

    @Override
    public void stop(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

    }

    @Override
    public void subscribe(Context context, String channel, Class<? extends Activity> cls) {
        PushService.subscribe(context, channel, cls);
    }

    @Override
    public void unsubscribe(Context context, String channel) {
        PushService.unsubscribe(context, channel);
    }
}
