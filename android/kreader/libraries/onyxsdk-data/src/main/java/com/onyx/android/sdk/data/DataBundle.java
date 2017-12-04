package com.onyx.android.sdk.data;

import org.greenrobot.eventbus.EventBus;

public class DataBundle {
    private EventBus eventBus = new EventBus();
    private CloudManager cloudManager = new CloudManager();

    public CloudManager getCloudManager() {
        return cloudManager;
    }

    public void setCloudManager(CloudManager cloudManager) {
        this.cloudManager = cloudManager;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

}
