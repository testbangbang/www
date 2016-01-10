package com.onyx.reader.text;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 1/10/16.
 */
public class TextLayoutContext {

    private RectF limitedRect = new RectF();
    private float availableWidth;
    private float availableHeight;
    private List<LayoutLine> layoutLines;
    private LayoutLine currentLine;


    public void initializeWithLimitedRect(final RectF rect) {
        limitedRect.set(rect);
        availableWidth = rect.width();
        availableHeight = rect.height();
        layoutLines = new ArrayList<LayoutLine>();
    }

    public LayoutLine createLayoutLine() {
        currentLine = new LayoutLine();
        currentLine.initialize(limitedRect, 1);
        return currentLine;
    }

    public LayoutLine nextLayoutLine(){
        verify();
        float prevLineHeight = currentLine.getContentHeight() * getLineSpacing();
        float newLinePosition = currentLine.getYPosition() + prevLineHeight;
        addLine(prevLineHeight);
        createLayoutLine();
        currentLine.setLinePosition(getLimitedRect().left, newLinePosition);
        return currentLine;
    }

    public float getLineSpacing() {
        return 1.5f;
    }

    public LayoutLine getCurrentLine() {
        return currentLine;
    }

    public final List<LayoutLine> getLayoutLines() {
        return layoutLines;
    }

    public void averageCurrentLineSpacing() {
        getCurrentLine().averageSpacing(getLimitedRect().left, getLimitedRect().width());
    }

    public final RectF getLimitedRect() {
        return limitedRect;
    }

    /**
     * check the required measureWidth at first, if there is no space to use, return false.
     * borrow measureWidth from spacing but make sure the minimum spacing large than the threshold.
     * @param element current element to process.
     * @return possible or not.
     */
    public boolean borrowFromSpacing(final Element element) {
        if (getCurrentLine().getContentWidth() + element.measureWidth() >= getLimitedRect().width()) {
            return false;
        }

        float requiredWidth = element.measureWidth() - getAvailableWidth();
        float totalSpacingWidth = getCurrentLine().totalSpacingWidth();
        if (totalSpacingWidth < requiredWidth) {
            return false;
        }
        if ((totalSpacingWidth - requiredWidth) / (float)getCurrentLine().spacingCount() < minElementSpacing()) {
            return false;
        }
        return true;
    }

    public float minElementSpacing() {
        return 12;
    }

    public float addElement(final Element element) {
        currentLine.addElement(element);
        if (availableWidth < element.measureWidth()) {
            availableWidth = 0;
        }
        availableWidth -= element.measureWidth();
        return availableWidth;
    }

    public float addLine(final float height) {
        availableHeight -= height;
        availableWidth = limitedRect.width();
        return availableHeight;
    }

    public float getAvailableWidth() {
        return availableWidth;
    }

    public float getAvailableHeight() {
        return availableHeight;
    }

    private void verify() {
        if (currentLine.getContentWidth() > getLimitedRect().width()) {
        }
    }
}
