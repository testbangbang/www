package com.onyx.reader.text;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zengzhu on 1/9/16.
 */
public class TextLayout {

    private TextLayoutContext textLayoutContext = new TextLayoutContext();
    private LayoutLine currentLine;


    /**
     * Algorithm: try element at first, add the element if possible to layout
     * if there is no enough measureWidth, try to compare spacing and required measureWidth
     * if required measureWidth less than spacing * 0.3, reduce the spacing of this line
     * otherwise try to hypernate and adjust spacing
     * if hypernate is not possible, move to next line and adjust spacing
     * @param rect the layout rect.
     * @param list the element list
     * @return layout lines
     */
    public List<LayoutLine> layoutElementsAdjusted(final RectF rect, final List<Element> list) {
        textLayoutContext.initializeOriginRect(rect);
        List<LayoutLine> layoutLines = new ArrayList<LayoutLine>();
        createLayoutLine().initialize(rect, 1);

        Element element = null;
        Iterator<Element> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (element == null) {
                element = iterator.next();
            }
            if (element.layout(textLayoutContext)) {
                addElement(textLayoutContext, element);
                element = iterator.next();
            } else if (borrowFromSpacing(textLayoutContext, currentLine, element)) {
                addElement(textLayoutContext, element);
                averageLineSpacing(currentLine, textLayoutContext);
                nextLayoutLine();
                element = iterator.next();
            } else if (element.breakElement(textLayoutContext.getLeftWidth(), textLayoutContext.getLeftHeight()) != null){
                addElement(textLayoutContext, element);
                averageLineSpacing(currentLine, textLayoutContext);
                nextLayoutLine();
                element = iterator.next();
            } else {
                averageLineSpacing(currentLine, textLayoutContext);
                nextLayoutLine();
            }
        }
        processLastLine();
        averageParagraphSpacing(layoutLines);
        return layoutLines;
    }


    private LayoutLine createLayoutLine() {
        currentLine = new LayoutLine();
        return currentLine;
    }

    private void addElement(final TextLayoutContext textLayoutContext, final Element element) {
        currentLine.addElement(textLayoutContext, element);
        textLayoutContext.addElement(element.measureWidth());
    }

    private LayoutLine nextLayoutLine() {
        if (currentLine.getLineWidth() > textLayoutContext.getOriginRect().width()) {
            int i = 0;
        }
        float lineHeight = currentLine.getLineHeight();
        textLayoutContext.addLine(lineHeight);
        float position = currentLine.getYPosition() + currentLine.getLineHeight();
        createLayoutLine();
        currentLine.setLinePosition(textLayoutContext.getOriginRect().left, position);
        return currentLine;
    }

    private void processLastLine() {
        averageLineSpacing(currentLine, textLayoutContext);
    }

    /**
     * check the required measureWidth at first, if there is no space to use, return false.
     * borrow measureWidth from spacing but make sure the minimum spacing large than the threshold.
     * @param textLayoutContext the layout context.
     * @param layoutLine current layout line.
     * @param element current element to process.
     * @return possible or not.
     */
    private boolean borrowFromSpacing(final TextLayoutContext textLayoutContext, final LayoutLine layoutLine, final Element element) {
        if (layoutLine.getLineWidth() + element.measureWidth() >= textLayoutContext.getOriginRect().width()) {
            return false;
        }

        float requiredWidth = element.measureWidth() - textLayoutContext.getLeftWidth();
        float totalSpacingWidth = layoutLine.totalSpacingWidth();
        if (totalSpacingWidth < requiredWidth) {
            return false;
        }
        if ((totalSpacingWidth - requiredWidth) / (float)layoutLine.spacingCount() < minElementSpacing()) {
            return false;
        }
        return true;
    }

    private float minElementSpacing() {
        return 12;
    }

    private void averageLineSpacing(final LayoutLine line, final TextLayoutContext textLayoutContext) {
        line.averageSpacing(textLayoutContext.getOriginRect().left, textLayoutContext.getOriginRect().width());
    }

    private void averageParagraphSpacing(final List<LayoutLine> list) {
    }

}
