package com.onyx.edu.reader.ui.events;

import android.graphics.RectF;

/**
 * Created by zhuzeng on 9/25/16.
 */

public class ScribbleMenuChangedEvent {

    private int bottomOfTopToolBar;
    private int topOfBottomToolBar;

    private RectF excludeRect;

    public ScribbleMenuChangedEvent(int bottomOfTopToolBar, int topOfBottomToolBar, RectF excludeRect) {
        this.bottomOfTopToolBar = bottomOfTopToolBar;
        this.topOfBottomToolBar = topOfBottomToolBar;
        this.excludeRect = excludeRect;
    }

    public static ScribbleMenuChangedEvent create(int bottomOfTopToolBar, int topOfBottomToolBar, RectF excludeRect) {
        return new ScribbleMenuChangedEvent(bottomOfTopToolBar, topOfBottomToolBar, excludeRect);
    }

    public int getBottomOfTopToolBar() {
        return bottomOfTopToolBar;
    }

    public int getTopOfBottomToolBar() {
        return topOfBottomToolBar;
    }

    public RectF getExcludeRect() {
        return excludeRect;
    }
}
