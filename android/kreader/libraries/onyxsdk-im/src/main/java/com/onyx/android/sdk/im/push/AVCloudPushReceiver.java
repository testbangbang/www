package com.onyx.android.sdk.im.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.data.Message;
import com.onyx.android.sdk.utils.Debug;


/**
 * Created by ming on 2017/7/13.
 */

public class AVCloudPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String pushData = intent.getExtras().getString(Constant.AVOSCLOUD_DATA);
        Debug.d(getClass(), "onReceive: " + pushData);

        try {
            String channel = intent.getExtras().getString(Constant.AVOSCLOUD_CHANNEL);
            Message message = JSON.parseObject(pushData, Message.class);
            message.setChannel(channel);
            IMManager.getInstance().onReceivedPushMessage(message);
        } catch (Exception e) {
            if (Debug.getDebug()) {
                Debug.e(getClass(), e);
            }
        }
    }
}
