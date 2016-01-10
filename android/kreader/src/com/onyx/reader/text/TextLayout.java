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
     * Algorithm: try element at first, add the element to layout if possible.
     * if there is no enough room, try to compare spacing and required width
     * if required width less than spacing, reduce the spacing among words of this line
     * otherwise try to break element if possible and adjust spacing
     * if hyphenation is not possible, move to next line and adjust word spacing of current line.
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
                textLayoutContext.averageCurrentLineSpacing();
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
        textLayoutContext.averageCurrentLineSpacing();
        textLayoutContext.nextLayoutLine();
        return true;
    }

    private boolean breakElementFilter(final TextLayoutContext textLayoutContext, final Element element) {
        List<Element> list = element.breakElement(textLayoutContext.getAvailableWidth(), textLayoutContext.getAvailableHeight());
        if (list == null) {
            return false;
        }
        textLayoutContext.addElement(element);
        textLayoutContext.averageCurrentLineSpacing();
        textLayoutContext.nextLayoutLine();
        return true;
    }

    private void processLastLine() {
        textLayoutContext.averageCurrentLineSpacing();
    }

    private void averageParagraphSpacing(final TextLayoutContext textLayoutContext) {
    }

}
