package com.onyx.android.sdk.reader.host.impl;

import android.graphics.RectF;
import com.onyx.android.sdk.reader.api.ReaderSelection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 10/14/15.
 */
public class ReaderSelectionImpl extends ReaderSelection {

    private String pageName;
    private String pagePosition;
    private String startPosition;
    private String endPosition;
    private String text;
    private String leftText;
    private String rightText;
    private List<RectF> displayRects;

    public ReaderSelectionImpl() {
        super();
    }

    public static ReaderSelectionImpl create(String startInternalLocation, String endInternationalLocation, String string, double [] array) {
        ReaderSelectionImpl object = new ReaderSelectionImpl();
        object.text = string;
        return object;
    }

    public ReaderSelectionImpl(List<RectF> displayRects, String endPosition, String leftText, String pageName, String pagePosition, String rightText, String startPosition, String text) {
        this.displayRects = displayRects;
        this.endPosition = endPosition;
        this.leftText = leftText;
        this.pageName = pageName;
        this.pagePosition = pagePosition;
        this.rightText = rightText;
        this.startPosition = startPosition;
        this.text = text;
    }

    @Override
    public ReaderSelectionImpl clone() {
        List<RectF> copy = new ArrayList<>();
        for (RectF displayRect : this.displayRects) {
            copy.add(new RectF(displayRect));
        }
        return new ReaderSelectionImpl(copy, endPosition, leftText, pageName, pagePosition, rightText, startPosition, text);
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
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
    }

    @Override
    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
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
