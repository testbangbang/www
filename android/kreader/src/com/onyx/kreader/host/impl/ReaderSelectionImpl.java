package com.onyx.kreader.host.impl;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderPagePosition;
import com.onyx.kreader.api.ReaderSelection;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class ReaderSelectionImpl implements ReaderSelection {

    private ReaderPagePosition startPosition;
    private ReaderPagePosition endPosition;
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

    public final ReaderPagePosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(final ReaderPagePosition l) {
        startPosition = l;
    }

    public final ReaderPagePosition getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(final ReaderPagePosition l) {
        endPosition = l;
    }

    public final String getText() {
        return text;
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
