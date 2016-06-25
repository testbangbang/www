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

    private PageNameList pageNameList = new PageNameList();
    private int currentPageIndex;
    public boolean canUndoShape;
    public boolean canRedoShape;

    public final PageNameList getPageNameList() {
        return pageNameList;
    }

    public boolean hasShapes() {
        return (pageNameList.size() > 0);
    }

    public void updateShapePageMap(final PageNameList pageNameList, int currentPage) {
        pageNameList.addAll(pageNameList.getPageNameList());
        currentPageIndex = currentPage;
    }

    public int getPageCount() {
        return pageNameList.size();
    }

    public int getCurrentPageIndex() {
        return getCurrentPageIndex();
    }

}
