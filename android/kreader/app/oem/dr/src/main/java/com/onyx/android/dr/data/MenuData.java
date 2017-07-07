package com.onyx.android.dr.data;

/**
 * Created by hehai on 17-6-28.
 */

public class MenuData {
    private String tabKey;
    private String tabName;
    private int imageResources;
    private Object eventBean;

    public MenuData(String tabKey, String tabName, int imageResources, Object eventBean) {
        this.tabKey = tabKey;
        this.tabName = tabName;
        this.imageResources = imageResources;
        this.eventBean = eventBean;
    }

    public String getTabName() {
        return tabName;
    }

    public int getImageResources() {
        return imageResources;
    }

    public Object getEventBean() {
        return eventBean;
    }

    public String getTabKey() {
        return tabKey;
    }
}
