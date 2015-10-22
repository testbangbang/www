package com.onyx.reader.host.impl;

import android.graphics.Rect;
import android.graphics.RectF;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderSelection;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class ReaderSelectionImpl implements ReaderSelection {

    private ReaderDocumentPosition startPosition;
    private ReaderDocumentPosition endPosition;
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

    public final ReaderDocumentPosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(final ReaderDocumentPosition l) {
        startPosition = l;
    }

    public final ReaderDocumentPosition getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(final ReaderDocumentPosition l) {
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
