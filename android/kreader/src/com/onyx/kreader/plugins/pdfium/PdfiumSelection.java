package com.onyx.kreader.plugins.pdfium;

import android.graphics.RectF;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.utils.PagePositionUtils;
import com.onyx.kreader.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 2/14/16.
 * javap -classpath ./bin/classes:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar:./com/onyx/kreader/plugins/pdfium/ -s com.onyx.kreader.plugins.pdfium.PdfiumSelection
 */
public class PdfiumSelection implements ReaderSelection {

    private int startCharIndex;
    private int endCharIndex;
    private String text;
    private List<RectF> rectangles = new ArrayList<RectF>();

    public PdfiumSelection() {
    }

    public String getStartPosition() {
        return PagePositionUtils.fromPosition(startCharIndex);
    }

    public String getEndPosition() {
        return PagePositionUtils.fromPosition(endCharIndex);
    }

    public String getText() {
        return text;
    }

    public int getStartCharIndex() {
        return startCharIndex;
    }

    public int getEndCharIndex() {
        return endCharIndex;
    }

    public void setRange(int start, int end) {
        startCharIndex = start;
        endCharIndex = end;
    }

    public void setText(final byte[] data) {
        text = StringUtils.utf16le(data);
    }

    public List<RectF> getRectangles() {
        return rectangles;
    }

    public SelectionType getSelectionType() {
        return SelectionType.TEXT;
    }

    public void addRectangle(int left, int top, int right, int bottom) {
        rectangles.add(new RectF(left, top, right, bottom));
    }
}
