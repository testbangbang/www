package com.onyx.android.sdk.scribble.request;

import android.content.Context;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.PageNameList;
import com.onyx.android.sdk.scribble.data.ShapeDataProvider;
import com.onyx.android.sdk.scribble.data.ShapeModel;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 6/7/16.
 */
public class ShapeDataInfo {

    private volatile PageNameList pageNameList = new PageNameList();
    private int currentPageIndex;
    private boolean canUndoShape;
    private boolean canRedoShape;
    private int background;
    private float eraserRadius;

    public final PageNameList getPageNameList() {
        return pageNameList;
    }

    public boolean hasShapes() {
        return (pageNameList.size() > 0);
    }

    public void updateShapePageMap(final PageNameList src, int currentPage) {
        pageNameList.addAll(src.getPageNameList());
        currentPageIndex = currentPage;
    }

    public void setBackground(final int bg) {
        background = bg;
    }

    public int getBackground() {
        return background;
    }

    public int getPageCount() {
        return pageNameList.size();
    }

    public int getCurrentPageIndex() {
        return currentPageIndex;
    }

    public boolean isCanUndoShape() {
        return canUndoShape;
    }

    public void setCanUndoShape(boolean canUndoShape) {
        this.canUndoShape = canUndoShape;
    }

    public boolean isCanRedoShape() {
        return canRedoShape;
    }

    public void setCanRedoShape(boolean canRedoShape) {
        this.canRedoShape = canRedoShape;
    }

    public float getEraserRadius() {
        return eraserRadius;
    }

    public void setEraserRadius(float eraserRadius) {
        this.eraserRadius = eraserRadius;
    }

}
