package com.onyx.kreader.plugins.neopdf;

import android.graphics.RectF;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.host.impl.ReaderTextSplitterImpl;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 2/14/16.
 * javap -classpath ./bin/classes:/opt/adt-bundle-linux/sdk/platforms/android-8/android.jar:./com/onyx/kreader/plugins/pdfium/ -s com.onyx.kreader.plugins.pdfium.NeoPdfSelection
 */
public class NeoPdfSelection implements ReaderSelection, Cloneable {

    private String pagePosition;
    private int startCharIndex;
    private int endCharIndex;
    private String text;
    private String leftText;
    private String rightText;

    /**
     * rectangles in page's coordinate from left-top origin
     */
    private List<RectF> rectangles = new ArrayList<RectF>();

    public NeoPdfSelection() {

    }

    public NeoPdfSelection(int pageNumber) {
        this.pagePosition = PagePositionUtils.fromPageNumber(pageNumber);
    }

    public NeoPdfSelection(String pagePosition) {
        this.pagePosition = pagePosition;
    }

    public NeoPdfSelection(int page, int [] data, byte [] string, int start, int end, String leftText, String rightText) {
        pagePosition = PagePositionUtils.fromPosition(page);
        for(int i = 0; i < data.length / 4; ++i) {
            rectangles.add(new RectF(data[i * 4], data[i * 4 + 1], data[i * 4 + 2], data[i * 4 + 3]));
        }
        text = StringUtils.utf16le(string);
        startCharIndex = start;
        endCharIndex = end;
        this.leftText = leftText;
        this.rightText = rightText;
    }

    @Override
    protected NeoPdfSelection clone() {
        List<RectF> rectList = new ArrayList<>();
        for (RectF rect : rectangles) {
            rectList.add(new RectF(rect));
        }
        NeoPdfSelection selection = new NeoPdfSelection();
        selection.pagePosition = pagePosition;
        selection.startCharIndex = startCharIndex;
        selection.endCharIndex = endCharIndex;
        selection.text = text;
        selection.leftText = leftText;
        selection.rightText = rightText;
        selection.rectangles = rectList;
        return selection;
    }

    public String getPagePosition() {
        return pagePosition;
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

    public String getLeftText() {
        return leftText;
    }

    public String getRightText() {
        return rightText;
    }

    @Override
    public boolean isSelectedOnWord() {
        return ReaderTextSplitterImpl.sharedInstance().isWord(getText());
    }

    @SuppressWarnings("unused")
    public int getStartCharIndex() {
        return startCharIndex;
    }

    @SuppressWarnings("unused")
    public int getEndCharIndex() {
        return endCharIndex;
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    public void addRectangle(int left, int top, int right, int bottom) {
        rectangles.add(new RectF(left, top, right, bottom));
    }

    @SuppressWarnings("unused")
    public static void addToSelectionList(List<ReaderSelection> list, int page, int [] rectangles, final byte[] utf16le, int startIndex, int endIndex, String leftText, String rightText) {
        list.add(new NeoPdfSelection(page, rectangles, utf16le, startIndex, endIndex, leftText, rightText));
    }

}
