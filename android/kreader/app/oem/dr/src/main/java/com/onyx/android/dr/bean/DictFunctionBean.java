package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class DictFunctionBean {
    private String tabName;
    private int imageResources;
    private Object eventBean;

    public DictFunctionBean(String tabName, Object eventBean) {
        this.tabName = tabName;
        this.eventBean = eventBean;
    }

    public DictFunctionBean(String tabName, int imageResources, Object eventBean) {
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
}
