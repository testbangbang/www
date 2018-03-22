package com.onyx.jdread.setting.event;

import android.os.Handler;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/12/28.
 */

public class DelayEvent {
    private long delayTime;

    public DelayEvent(long millisecond) {
        this.delayTime = millisecond;
    }

    public void post() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(DelayEvent.this);
            }
        }, delayTime);
    }
}
