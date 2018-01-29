package com.onyx.jdread.reader.menu.event;

import com.onyx.jdread.reader.actions.ToggleBookmarkAction;

/**
 * Created by huxiaomao on 2018/1/27.
 */

public class ToggleBookmarkSuccessEvent {
    private ToggleBookmarkAction.ToggleSwitch toggleSwitch;

    public ToggleBookmarkSuccessEvent(ToggleBookmarkAction.ToggleSwitch toggleSwitch) {
        this.toggleSwitch = toggleSwitch;
    }

    public ToggleBookmarkAction.ToggleSwitch getToggleSwitch() {
        return toggleSwitch;
    }
}
