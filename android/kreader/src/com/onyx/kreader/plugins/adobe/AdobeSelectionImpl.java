package com.onyx.kreader.plugins.adobe;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderSelection;

import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class AdobeSelectionImpl implements ReaderSelection {

    private String start, end;
    private String text;
    private List<RectF> rectangles;

    public AdobeSelectionImpl() {

    }

    public void setStartPosition(final String s) {
        start = s;
    }

    public String getStartPosition() {
        return start;
    }

    public void setEndPosition(final String e) {
        end = e;
    }

    public String getEndPosition() {
        return end;
    }

    public void setText(final String t) {
        text = t;
    }

    public String getText() {
        return text;
    }

    public void setRectangles(final List<RectF> list) {
        rectangles = list;
    }

    public List<RectF> getRectangles() {
        return rectangles;
    }

    public SelectionType getSelectionType() {
        return null;
    }

}
