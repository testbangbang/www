package com.onyx.reader.plugins.adobe;

import android.graphics.RectF;
import com.onyx.reader.api.ReaderDocumentPosition;
import com.onyx.reader.api.ReaderSelection;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class AdobeSelectionImpl implements ReaderSelection {

    private AdobeDocumentPositionImpl start, end;
    private String text;
    private List<RectF> rectangles;

    public AdobeSelectionImpl() {

    }

    public void setStartPosition(final AdobeDocumentPositionImpl s) {
        start = s;
    }

    public ReaderDocumentPosition getStartPosition() {
        return start;
    }

    public void setEndPosition(final AdobeDocumentPositionImpl e) {
        end = e;
    }

    public ReaderDocumentPosition getEndPosition() {
        return end;
    }

    public void setText(final String t) {
        text = t;
    }

    public String getText() {
        return null;
    }

    public void setRectangles(final List<RectF> list) {
        rectangles = list;
    }

    public List<RectF> getRectangles() {
        return null;
    }


}
