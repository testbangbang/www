package com.onyx.android.dr.data;

/**
 * Created by hehai on 17-7-15.
 */

public class ReaderMenuBean extends MenuBean {
    private boolean enable = true;

    public ReaderMenuBean(String tabKey, String tabName, int imageResources, Object eventBean) {
        super(tabKey, tabName, imageResources, eventBean);
    }

    public ReaderMenuBean(String tabKey, String tabName, String imageUrl, Object eventBean) {
        super(tabKey, tabName, imageUrl, eventBean);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
