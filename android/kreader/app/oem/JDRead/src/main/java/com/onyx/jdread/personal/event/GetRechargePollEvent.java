package com.onyx.jdread.personal.event;

import android.os.Handler;
import android.os.Message;

import com.onyx.jdread.personal.model.PersonalDataBundle;

import java.lang.ref.WeakReference;

/**
 * Created by li on 2018/1/31.
 */

public class GetRechargePollEvent {
    private String orderId;
    public final static int RECHARGE_POLL_WHAT = 0x1000;
    public final static int DEFAULT_DELAY_TIME = 1000;
    public final static int TWO_MILLIS = 2000;
    public final static int Thirty_MILLIS = 30000;
    private RechargeHandler handler;
    public static int pollTime;

    public GetRechargePollEvent(String orderId) {
        this.orderId = orderId;
        handler = new RechargeHandler(GetRechargePollEvent.this);
    }

    public void startPoll(int pollTime) {
        this.pollTime = pollTime;
        handler.sendEmptyMessageDelayed(RECHARGE_POLL_WHAT, DEFAULT_DELAY_TIME);
    }

    public void setPollTime(int pollTime) {
        this.pollTime = pollTime;
    }

    public String getOrderId() {
        return orderId;
    }

    private static class RechargeHandler extends Handler {
        private WeakReference<GetRechargePollEvent> pollEvent;

        public RechargeHandler(GetRechargePollEvent event) {
            pollEvent = new WeakReference<GetRechargePollEvent>(event);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RECHARGE_POLL_WHAT) {
                GetRechargePollEvent getRechargePollEvent = pollEvent.get();
                if (pollTime <= 0 || getRechargePollEvent == null) {
                    return;
                } else if (pollTime >= 300 - 30 - 180 && pollTime <= 300 - 30) {
                    pollTime -= 2;
                    PersonalDataBundle.getInstance().getEventBus().post(getRechargePollEvent);
                    sendEmptyMessageDelayed(RECHARGE_POLL_WHAT, TWO_MILLIS);
                } else if (pollTime < 300 - 30 - 180) {
                    pollTime -= 30;
                    PersonalDataBundle.getInstance().getEventBus().post(getRechargePollEvent);
                    sendEmptyMessageDelayed(RECHARGE_POLL_WHAT, Thirty_MILLIS);
                } else {
                    pollTime--;
                    sendEmptyMessageDelayed(RECHARGE_POLL_WHAT, DEFAULT_DELAY_TIME);
                }
            }
        }
    }
}
