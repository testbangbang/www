package com.onyx.android.dr.bean;

import java.io.Serializable;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public class DictTypeBean implements Serializable {
    private String tabName;
    private Object eventBean;
    private int type;

    public DictTypeBean(String tabName) {
        this.tabName = tabName;
    }

    public DictTypeBean(String tabName, Object eventBean, int type) {
        this.tabName = tabName;
        this.eventBean = eventBean;
        this.type = type;
    }

    public String getTabName() {
        return tabName;
    }

    public Object getEventBean() {
        return eventBean;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
