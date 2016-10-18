package com.onyx.android.sdk.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/10/18.
 */

public class DeviceConfig {

    private List<String> disable_menu;

    public List<String> getDisable_menu() {
        return disable_menu;
    }

    public void setDisable_menu(List<String> disable_menu) {
        this.disable_menu = disable_menu;
    }

    public List<ReaderMenuAction> getDisableMenus() {
        List<ReaderMenuAction> readerMenuActions = new ArrayList<>();
        if (disable_menu != null && disable_menu.size() > 0) {
            for (String ignore : disable_menu) {
                readerMenuActions.add(ReaderMenuAction.valueOf(ignore));
            }
        }
        return readerMenuActions;
    }
}
