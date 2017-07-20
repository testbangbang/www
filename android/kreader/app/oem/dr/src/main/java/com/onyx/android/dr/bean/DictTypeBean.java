package com.onyx.android.dr.bean;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class DictTypeBean {
    private String tabName;
    private Object eventBean;

    public DictTypeBean(String tabName) {
        this.tabName = tabName;
    }

    public DictTypeBean(String tabName, Object eventBean) {
        this.tabName = tabName;
        this.eventBean = eventBean;
    }

    public String getTabName() {
        return tabName;
    }

    public Object getEventBean() {
        return eventBean;
    }
}
