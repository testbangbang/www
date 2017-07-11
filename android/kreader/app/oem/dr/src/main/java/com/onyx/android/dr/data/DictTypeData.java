package com.onyx.android.dr.data;

/**
 * Created by zhouzhiming on 17-6-28.
 */

public class DictTypeData {
    private String tabName;
    private Object eventBean;

    public DictTypeData(String tabName) {
        this.tabName = tabName;
    }

    public DictTypeData(String tabName, Object eventBean) {
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
