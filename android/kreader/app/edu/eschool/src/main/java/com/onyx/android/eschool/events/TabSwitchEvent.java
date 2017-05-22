package com.onyx.android.eschool.events;

/**
 * Created by suicheng on 2017/5/21.
 */

public class TabSwitchEvent {
    public static int NEXT_TAB = 0;
    public static int PREV_TAB = 1;

    public int switchTo = NEXT_TAB;

    private TabSwitchEvent(int switchTo) {
        this.switchTo = switchTo;
    }

    public static TabSwitchEvent createNextTabSwitch() {
        return new TabSwitchEvent(NEXT_TAB);
    }

    public static TabSwitchEvent createPrevTabSwitch() {
        return new TabSwitchEvent(PREV_TAB);
    }

    public boolean isNextTabSwitch() {
        return switchTo == NEXT_TAB;
    }
}
