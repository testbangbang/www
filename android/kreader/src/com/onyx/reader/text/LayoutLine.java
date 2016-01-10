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
    private float lineHeight = 0;
    private float lineWidth = 0;
    private float currentX;
    private float currentY;
    private int direction = 1;

    private int getDirection() {
        return direction;
    }

    public void initialize(final RectF rect, int directionValue) {
        currentX = rect.left;
        currentY = 0;
        direction = directionValue;
        lineWidth = 0;
    }

    public void setLinePosition(final float x, final float y) {
        currentX = x;
        currentY = y;
    }

    public void addElement(final Element element) {
        element.setPosition(currentX, currentY);
        elementList.add(element);
        spacingList.add(element.spacing());
        currentX += getDirection() * element.measureWidth();
        currentX += getDirection() * element.spacing();
        if (lineHeight < element.measureHeight()) {
            lineHeight = element.measureHeight();
        }
        lineWidth += element.measureWidth();
        lineWidth += element.spacing();
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public float getYPosition() {
        return currentY;
    }

    public float totalSpacingWidth() {
        float width = 0.0f;
        for(Float value : spacingList) {
            width += value;
        }
        return width;
    }

    public int spacingCount() {
        return spacingList.size();
    }

    public void averageSpacing(final float totalWidth) {
        float leftSpace = totalWidth - lineWidth;
        if (leftSpace <= 0) {
            return;
        }
        if (spacingCount() <= 1) {
            return;
        }
        float average = leftSpace / (spacingCount() - 1);
        float left = 0;
        for(Element element : elementList) {
            element.setX(left);
            left += element.measureWidth();
            left += average;
        }
    }

}
