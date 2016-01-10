package com.onyx.reader.text;

import android.graphics.RectF;

import java.util.Iterator;
import java.util.List;

/**
 * Created by zengzhu on 1/9/16.
 */
public class TextLayout {

    private TextLayoutContext textLayoutContext = new TextLayoutContext();

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
        textLayoutContext.initializeWithLimitedRect(rect);
        textLayoutContext.createLayoutLine();

        Element element = null;
        Iterator<Element> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (element == null) {
                element = iterator.next();
            }
            if (elementFilter(textLayoutContext, element)) {
                element = iterator.next();
            } else if (spacingFilter(textLayoutContext, element)) {
                element = iterator.next();
            } else if (breakElementFilter(textLayoutContext, element)){
                element = iterator.next();
            } else {
                textLayoutContext.averageLineSpacing();
                textLayoutContext.nextLayoutLine();
            }
        }
        processLastLine();
        averageParagraphSpacing(textLayoutContext);
        return textLayoutContext.getLayoutLines();
    }

    private boolean elementFilter(final TextLayoutContext textLayoutContext, final Element element) {
        if (!element.layout(textLayoutContext)) {
            return false;
        }
        textLayoutContext.addElement(element);
        return true;
    }

    private boolean spacingFilter(final TextLayoutContext textLayoutContext, final Element element) {
        if (!textLayoutContext.borrowFromSpacing(element)) {
            return false;
        }
        textLayoutContext.addElement(element);
        textLayoutContext.averageLineSpacing();
        textLayoutContext.nextLayoutLine();
        return true;
    }

    private boolean breakElementFilter(final TextLayoutContext textLayoutContext, final Element element) {
        List<Element> list = element.breakElement(textLayoutContext.getAvailableWidth(), textLayoutContext.getAvailableHeight());
        if (list == null) {
            return false;
        }
        textLayoutContext.addElement(element);
        textLayoutContext.averageLineSpacing();
        textLayoutContext.nextLayoutLine();
        return true;
    }

    private void processLastLine() {
        textLayoutContext.averageLineSpacing();
    }

    private void averageParagraphSpacing(final TextLayoutContext textLayoutContext) {
    }

}
