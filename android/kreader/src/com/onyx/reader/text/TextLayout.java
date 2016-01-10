package com.onyx.reader.text;

import android.graphics.RectF;

import java.util.ArrayList;
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
        for(Element element : list) {
            if (element.layout(textLayoutContext)) {
                addElement(element);
                continue;
            } else if (possibleToAdjustSpacing(textLayoutContext, currentLine, element)) {
                addElement(element);
                nextLayoutLine();
                continue;
            } else if (element.breakElement(textLayoutContext.getLeftWidth(), textLayoutContext.getLeftHeight()) != null){
                averageLineSpacing(currentLine, textLayoutContext);
                addElement(element);
                nextLayoutLine();
                continue;
            } else {
                averageLineSpacing(currentLine, textLayoutContext);
                nextLayoutLine();
                continue;
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

    private void addElement(final Element element) {
        currentLine.addElement(element);
        textLayoutContext.addElement(element.measureWidth());
    }

    private LayoutLine nextLayoutLine() {
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
    private boolean possibleToAdjustSpacing(final TextLayoutContext textLayoutContext, final LayoutLine layoutLine, final Element element) {
        float requiredWidth = element.measureWidth() - textLayoutContext.getLeftWidth();
        if (layoutLine.totalSpacingWidth() < requiredWidth) {
            return false;
        }
        if (layoutLine.totalSpacingWidth() - requiredWidth / layoutLine.spacingCount() < 5) {
            return false;
        }
        return true;
    }

    private void averageLineSpacing(final LayoutLine line, final TextLayoutContext textLayoutContext) {
        line.averageSpacing(textLayoutContext.getOriginRect().width());
    }

    private void averageParagraphSpacing(final List<LayoutLine> list) {
    }

}
