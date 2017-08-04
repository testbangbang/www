package com.onyx.android.monitor.event;

import com.onyx.android.monitor.view.MenuItem;

/**
 * Created by wangxu on 17-7-28.
 */

public class SettingsChangedEvent {
    private MenuItem.MenuId id;
    public MenuItem.MenuId getId() {
        return id;
    }

    public SettingsChangedEvent(MenuItem.MenuId id) {
        this.id = id;
    }
}
