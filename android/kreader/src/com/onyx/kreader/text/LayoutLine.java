package com.onyx.kreader.text;

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

    public final List<Element> getElementList() {
        return elementList;
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

    private float updateContentWidth() {
        contentWidth = 0;
        contentHeight = 0;
        totalSpacing = 0;
        for(Element element : elementList) {
            contentWidth += element.measureWidth();
            totalSpacing += element.spacing();
            if (contentHeight < element.measureHeight()) {
                contentHeight = element.measureHeight();
            }
        }
        return contentWidth;
    }

    public Element removeLastElement() {
        int size = elementList.size();
        if (size <= 0) {
            return null;
        }
        spacingList.remove(size - 1);
        return elementList.remove(size - 1);
    }

    public boolean isEmpty() {
        return elementList.isEmpty();
    }

    /**
     * @return content width without spacing.
     */
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
        updateContentWidth();
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

    /**
     * TODO: consider latin
     * @param originLeft
     * @param totalWidth
     */
    public void alignToLeft(final float originLeft, final float totalWidth) {
        updateContentWidth();
        float leftSpace = totalWidth - contentWidth;
        if (leftSpace <= 0) {
            return;
        }

        float left = originLeft;
        for(Element element : elementList) {
            element.setElementX(left);
            left += element.measureWidth();
        }
    }

    public boolean hasElementCanBePlacedAtLineBegin() {
        for(Element element : elementList) {
            if (element.canBeLayoutedAtLineBegin()) {
                return true;
            }
        }
        return false;
    }

    public List<Element> getElementListBeforeLineBegin() {
        List<Element> temp = new ArrayList<Element>();
        while (!isEmpty()) {
            Element element = removeLastElement();
            if (element == null) {
                break;
            }
            temp.add(0, element);
            if (element.canBeLayoutedAtLineBegin()) {
                break;
            }
        }
        return temp;
    }

}
