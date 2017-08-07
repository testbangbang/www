package com.onyx.android.sdk.im.push;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.im.IMConfig;

/**
 * Created by ming on 2017/7/12.
 */

public interface BasePushService {

    void init(Context context, IMConfig imConfig);

    void start(Context context);

    void stop(Context context);

    void onReceive(Context context, Intent intent);

    void subscribe(Context context, String channel, Class<? extends Activity> cls);

    void unsubscribe(Context context, String channel);
}
