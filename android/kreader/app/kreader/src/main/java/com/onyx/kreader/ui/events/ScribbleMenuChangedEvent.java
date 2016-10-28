package com.onyx.kreader.ui.events;

import android.graphics.Rect;

/**
 * Created by zhuzeng on 9/25/16.
 */

public class ScribbleMenuChangedEvent {

    private int bottomOfTopToolBar;
    private int topOfBottomToolBar;

    private Rect excludeRect;

    public ScribbleMenuChangedEvent(int bottomOfTopToolBar, int topOfBottomToolBar, Rect excludeRect) {
        this.bottomOfTopToolBar = bottomOfTopToolBar;
        this.topOfBottomToolBar = topOfBottomToolBar;
        this.excludeRect = excludeRect;
    }

    public static ScribbleMenuChangedEvent create(int bottomToolBarTop, int topToolBarBottom, Rect excludeRect) {
        return new ScribbleMenuChangedEvent(bottomToolBarTop, topToolBarBottom, excludeRect);
    }

    public int getBottomOfTopToolBar() {
        return bottomOfTopToolBar;
    }

    public int getTopOfBottomToolBar() {
        return topOfBottomToolBar;
    }

    public Rect getExcludeRect() {
        return excludeRect;
    }
}
