package com.onyx.reader.text;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 1/9/16.
 */
public class LayoutLine {

    private List<Element> elementList = new ArrayList<Element>();
    private List<Float> spacingList = new ArrayList<Float>();
    private float contentHeight = 0;
    private float contentWidth = 0;
    private float totalSpacing = 0;
    private float x;
    private float y;
    private int direction = 1;


    private int getDirection() {
        return direction;
    }

    public void initialize(final RectF rect, int directionValue) {
        x = rect.left;
        y = rect.top;
        direction = directionValue;
        contentWidth = 0;
    }

    public void setLinePosition(final float px, final float py) {
        x = px;
        y = py;
    }

    public void addElement(final Element element) {
        element.setElementPosition(x, y);
        elementList.add(element);
        spacingList.add(element.spacing());
        totalSpacing += element.spacing();
        x += getDirection() * element.measureWidth();
        x += getDirection() * element.spacing();
        if (contentHeight < element.measureHeight()) {
            contentHeight = element.measureHeight();
        }
        contentWidth += element.measureWidth();
    }

    public float getContentWidth() {
        return contentWidth;
    }

    public float getContentHeight() {
        return contentHeight;
    }

    public float getYPosition() {
        return y;
    }

    public float totalSpacingWidth() {
        return totalSpacing;
    }

    public int spacingCount() {
        return spacingList.size();
    }

    public void averageSpacing(final float originLeft, final float totalWidth) {
        float leftSpace = totalWidth - contentWidth;
        if (leftSpace <= 0) {
            return;
        }
        if (spacingCount() <= 1) {
            return;
        }
        float average = leftSpace / (spacingCount() - 1);
        float left = originLeft;
        for(Element element : elementList) {
            element.setElementX(left);
            left += element.measureWidth();
            left += average;
        }
    }

}
