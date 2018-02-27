package com.onyx.jdread.personal.cloud.entity.jdbean;

import com.onyx.jdread.personal.event.ReceivePackageEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by li on 2018/2/26.
 */

public class GiftDetailBean {
    public String packageName;
    public String packageDetail;
    private EventBus eventBus;

    public void onReceivePackage() {
        eventBus.post(new ReceivePackageEvent());
    }

    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }
}
