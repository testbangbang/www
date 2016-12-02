package com.onyx.kreader.host.impl;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderSelection;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class ReaderSelectionImpl implements ReaderSelection {

    private String pageName;
    private String pagePosition;
    private String startPosition;
    private String endPosition;
    private String text;
    private List<RectF> displayRects;

    public ReaderSelectionImpl() {
        super();
    }

    public static ReaderSelectionImpl create(String startInternalLocation, String endInternationalLocation, String string, double [] array) {
        ReaderSelectionImpl object = new ReaderSelectionImpl();
        object.text = string;
        return object;
    }

    @Override
    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPagePosition() {
        return pagePosition;
    }

    public void setPagePosition(String pagePosition) {
        this.pagePosition = pagePosition;
    }

    public final String getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(final String l) {
        startPosition = l;
    }

    public final String getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(final String l) {
        endPosition = l;
    }

    public final String getText() {
        return text;
    }

    @Override
    public String getLeftText() {
        return "";
    }

    @Override
    public String getRightText() {
        return "";
    }

    @Override
    public boolean isSelectedOnWord() {
        return ReaderTextSplitterImpl.sharedInstance().isWord(getText());
    }

    public boolean hasText() {
        return false;
    }

    public void setText(final String t) {
        text = t;
    }

    public void setDisplayRects(final List<RectF> list) {
        displayRects = list;
    }

    public final List<RectF> getRectangles() {
        return displayRects;
    }

    static public boolean isBlank(ReaderSelection result) {
        return  false;
        //return (result == null || result.getStartPosition().isBlankInternalLocation() || ReaderUtils.isBlankString(result.getText()));
    }

    public SelectionType getSelectionType() {
        return null;
    }

}
