package com.onyx.android.dr.reader.event;

import android.os.Message;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/1/12.
 */

public class LoadDictEvent {
    private static final int EVENT_TYPE_SEND_MESSAGE_DELAYED = 0x8000;
    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_TYPE_SEND_MESSAGE_DELAYED:
                    EventBus.getDefault().post(msg.obj);
                    break;
            }
        }
    };

    public void sendMessageDelayed(long delayMillis) {
        Message msg = new Message();
        msg.what = EVENT_TYPE_SEND_MESSAGE_DELAYED;
        msg.obj = this;
        handler.sendMessageDelayed(msg, delayMillis);
    }
}
