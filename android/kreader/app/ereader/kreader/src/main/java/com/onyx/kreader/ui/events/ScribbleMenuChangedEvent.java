package com.onyx.kreader.ui.events;

import android.graphics.RectF;

import java.util.List;

/**
 * Created by zhuzeng on 9/25/16.
 */

public class ScribbleMenuChangedEvent {

    private int bottomOfTopToolBar;
    private int topOfBottomToolBar;

    private List<RectF> excludeRects;

    public ScribbleMenuChangedEvent(int bottomOfTopToolBar, int topOfBottomToolBar, List<RectF> excludeRects) {
        this.bottomOfTopToolBar = bottomOfTopToolBar;
        this.topOfBottomToolBar = topOfBottomToolBar;
        this.excludeRects = excludeRects;
    }

    public ScribbleMenuChangedEvent(List<RectF> excludeRects) {
        this.excludeRects = excludeRects;
    }

    public static ScribbleMenuChangedEvent create(List<RectF> excludeRects) {
        return new ScribbleMenuChangedEvent(excludeRects);
    }

    public static ScribbleMenuChangedEvent create(int bottomToolBarTop, int topToolBarBottom, List<RectF> excludeRects) {
        return new ScribbleMenuChangedEvent(bottomToolBarTop, topToolBarBottom, excludeRects);
    }

    public int getBottomOfTopToolBar() {
        return bottomOfTopToolBar;
    }

    public int getTopOfBottomToolBar() {
        return topOfBottomToolBar;
    }

    public List<RectF> getExcludeRects() {
        return excludeRects;
    }
}
