package com.onyx.android.sdk.im.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyx.android.sdk.im.Constant;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.Message;
import com.onyx.android.sdk.utils.Debug;

import org.json.JSONObject;


/**
 * Created by ming on 2017/7/13.
 */

public class AVCloudPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Debug.d(getClass(), "onReceive: " + intent.getExtras().getString(Constant.AVOSCLOUD_DATA));

        try {
            String channel = intent.getExtras().getString(Constant.AVOSCLOUD_CHANNEL);
            JSONObject json = new JSONObject(intent.getExtras().getString(Constant.AVOSCLOUD_DATA));
            String id = json.getString(Constant.MESSAGE_ID);
            String content = json.getString(Constant.MESSAGE_CONTENT);
            String action = json.getString(Constant.MESSAGE_ACTION);
            Message message = Message.create(channel, action, id, content);
            IMManager.getInstance().onReceivedPushMessage(message);
        }catch (Exception e) {

        }
    }
}
