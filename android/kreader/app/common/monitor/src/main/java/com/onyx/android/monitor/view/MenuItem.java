package com.onyx.android.monitor.view;

/**
 * Created by wangxu on 17-7-24.
 */

public class MenuItem {

    public enum MenuId {CONTRAST, A2, FULL_REFRESH, BRIGHTNESS, EXIT, ORIENTATION}

    private MenuId id;
    private int drawableResourceId;

    public MenuItem(MenuId id, int resourceId) {
        this.id = id;
        this.drawableResourceId = resourceId;
    }

    public int getDrawableResourceId() {
        return this.drawableResourceId;
    }

    public MenuId getId() {
        return this.id;
    }
}
