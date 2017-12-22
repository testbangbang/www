package com.onyx.jdread.shop.event;

/**
 * Created by huxiaomao on 17/3/13.
 */

public class MenuWifiSettingEvent {
    String itemName;

    public MenuWifiSettingEvent(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
}
