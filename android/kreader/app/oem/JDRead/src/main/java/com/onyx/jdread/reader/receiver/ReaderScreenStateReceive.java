package com.onyx.jdread.reader.receiver;

import android.content.Context;
import android.content.Intent;

import com.onyx.jdread.main.receiver.ScreenStateReceive;
import com.onyx.jdread.reader.event.ScreenOffEvent;
import com.onyx.jdread.reader.event.ScreenOnEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/3/18.
 */

public class ReaderScreenStateReceive extends ScreenStateReceive {
    private EventBus eventBus;

    public ReaderScreenStateReceive(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SCREEN_ON.equals(intent.getAction())) {
            eventBus.post(new ScreenOnEvent());
            return;
        }
        if (SCREEN_OFF.equals(intent.getAction())) {
            eventBus.post(new ScreenOffEvent());
            return;
        }
    }
}
