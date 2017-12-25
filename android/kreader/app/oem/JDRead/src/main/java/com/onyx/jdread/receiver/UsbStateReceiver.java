package com.onyx.jdread.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.onyx.jdread.event.UsbDisconnectedEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by hehai on 17-12-15.
 */

public class UsbStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean connected = intent.getExtras().getBoolean("connected");
        if (!connected) {
            EventBus.getDefault().post(new UsbDisconnectedEvent());
        }
    }
}
