package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 7/29/16.
 */
public class ChangeOrientationEvent {
    private int orientation;

    public ChangeOrientationEvent(int newOrientation) {
        orientation = newOrientation;
    }

    public int getOrientation() {
        return orientation;
    }
}
