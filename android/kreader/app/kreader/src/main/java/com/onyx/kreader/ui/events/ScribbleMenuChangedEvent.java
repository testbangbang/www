package com.onyx.kreader.ui.events;

/**
 * Created by zhuzeng on 9/25/16.
 */

public class ScribbleMenuChangedEvent {

    private int bottomOfTopToolBar;
    private int topOfBottomToolBar;

    public ScribbleMenuChangedEvent(int bottomOfTopToolBar, int topOfBottomToolBar) {
        this.bottomOfTopToolBar = bottomOfTopToolBar;
        this.topOfBottomToolBar = topOfBottomToolBar;
    }

    public static ScribbleMenuChangedEvent create(int bottomToolBarTop, int topToolBarBottom) {
        return new ScribbleMenuChangedEvent(bottomToolBarTop, topToolBarBottom);
    }

    public int getBottomOfTopToolBar() {
        return bottomOfTopToolBar;
    }

    public int getTopOfBottomToolBar() {
        return topOfBottomToolBar;
    }
}
