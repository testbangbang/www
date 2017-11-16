package com.onyx.android.plato.event;

/**
 * Created by jackdeng on 2017/10/27.
 */

public class OnBackPressEvent {
    public int childViewId;
    public OnBackPressEvent(int childViewId) {
        this.childViewId = childViewId;
    }
}
