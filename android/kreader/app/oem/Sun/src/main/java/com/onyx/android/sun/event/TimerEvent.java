package com.onyx.android.sun.event;

import android.os.Handler;
import android.os.Message;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2017/10/16.
 */

public class TimerEvent {
    private static final int COUNT_DOWN_WHAT = 0x1000;
    private static final int EACH_SECOND = 1000;
    private int result;
    private int count;

    private Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COUNT_DOWN_WHAT:
                    handleCountDown();
                    break;
            }
        }
    };

    private void handleCountDown() {
        count--;
        TimerEvent timerEvent = new TimerEvent();
        timerEvent.setResult(count);
        EventBus.getDefault().post(timerEvent);
        if (count > 0) {
            handle.sendEmptyMessageDelayed(COUNT_DOWN_WHAT, EACH_SECOND);
        }
    }

    public void timeCountDown(int count) {
        this.count = count;
        handle.sendEmptyMessageDelayed(COUNT_DOWN_WHAT, EACH_SECOND);
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }
}
