package com.onyx.jdread.reader.highlight;

import android.graphics.PointF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 2018/1/16.
 */

public class SelectionInfo implements Cloneable {
    private ReaderSelection currentSelection;
    private List<HighlightCursor> cursors = new ArrayList<HighlightCursor>();
    private PointF highLightBeginTop;
    private PointF highLightEndBottom;
    private PointF touchPoint;
    public PageInfo pageInfo;
    public String pagePosition;
    private List<PageAnnotation> pageAnnotations = new ArrayList<>();

    public List<PageAnnotation> getPageAnnotations() {
        return pageAnnotations;
    }

    public void setPageAnnotations(List<PageAnnotation> pageAnnotations) {
        if(pageAnnotations == this.pageAnnotations){
            return;
        }
        this.pageAnnotations.clear();
        if(pageAnnotations != null) {
            this.pageAnnotations.addAll(pageAnnotations);
        }
    }

    @Override
    public SelectionInfo clone() {
        SelectionInfo copy = new SelectionInfo();
        copy.currentSelection = currentSelection.clone();
        copy.cursors = cursors;
        copy.highLightBeginTop = new PointF(highLightBeginTop.x, highLightBeginTop.y);
        copy.highLightEndBottom = new PointF(highLightEndBottom.x, highLightEndBottom.y);
        copy.touchPoint = new PointF(touchPoint.x, touchPoint.y);
        copy.pageInfo = new PageInfo(pageInfo);
        copy.pagePosition = pagePosition;
        return copy;
    }

    public PointF getTouchPoint() {
        return touchPoint;
    }

    public void setTouchPoint(PointF touchPoint) {
        this.touchPoint = touchPoint;
    }

    public ReaderSelection getCurrentSelection() {
        return currentSelection;
    }

    public void setCurrentSelection(ReaderSelection currentSelection,PageInfo pageInfo,List<PageAnnotation> pageAnnotations) {
        this.currentSelection = currentSelection;
        this.pageInfo = pageInfo;
        setPageAnnotations(pageAnnotations);
    }

    public List<HighlightCursor> getCursors() {
        return cursors;
    }

    public void setCursors(List<HighlightCursor> cursors) {
        this.cursors.clear();
        this.cursors.addAll(cursors);
    }

    public PointF getHighLightBeginTop() {
        return highLightBeginTop;
    }

    public void setHighLightBeginTop(PointF highLightBeginTop) {
        this.highLightBeginTop = highLightBeginTop;
    }

    public PointF getHighLightEndBottom() {
        return highLightEndBottom;
    }

    public void setHighLightEndBottom(PointF highLightEndBottom) {
        this.highLightEndBottom = highLightEndBottom;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(String pagePosition) {
        this.pagePosition = pagePosition;
    }
}
