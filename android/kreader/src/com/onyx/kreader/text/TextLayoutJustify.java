package com.onyx.kreader.text;

import android.graphics.RectF;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;

import java.util.List;

/**
 * Created by zengzhu on 1/9/16.
 */
public class TextLayoutJustify {

    /**
     * Algorithm: try element at first, add the element to layout if possible.
     * if there is no enough room, try to compare spacing and required width
     * if required width less than spacing, reduce the spacing among words of this line
     * otherwise try to break element if possible and adjust spacing
     * if hyphenation is not possible, move to next line and adjust word spacing of current line.
     * check punctuation if the first element is punctuation, move the last element of last line to the new line.
     * average the last line.
     * @param rect the layout rect.
     * @param list the element list
     * @return layout lines
     */
    public List<LayoutLine> layout(final RectF rect, final List<Element> list) {
        TextLayoutContext textLayoutContext = new TextLayoutContext();
        textLayoutContext.initializeWithLimitedRect(rect);
        textLayoutContext.createLayoutLine();
        textLayoutContext.setElementList(list, 0);

        onLayoutBegin();
        Element element;
        while ((element = textLayoutContext.getCurrentElement()) != null) {
            if (onNewElement(textLayoutContext, element)) {
                textLayoutContext.nextElement();
            } else if (onOutOfSpace(textLayoutContext, element)) {
                textLayoutContext.nextElement();
            } else {
                onNewLine(textLayoutContext, element);
            }
        }
        onLayoutFinished(textLayoutContext);
        return textLayoutContext.getLayoutLines();
    }

    /**
     * try to layout the entry if possible according to current context.
     * @param context
     * @param entry
     * @return
     */
    public boolean addEntryToLayout(final TextLayoutContext context, final ParagraphEntry entry) {
        return true;
    }

    private boolean onLayoutBegin() {
        return true;
    }

    private boolean onLayoutFinished(final TextLayoutContext textLayoutContext) {
        textLayoutContext.alignToLeft();
        textLayoutContext.averageVerticalLineSpacing();
        return true;
    }

    private boolean onNewElement(final TextLayoutContext textLayoutContext, final Element element) {
        if (!element.canLayout(textLayoutContext)) {
            return false;
        }
        textLayoutContext.addElement(element);
        return true;
    }

    /**
     * When out of space, we can try to:
     * 1. borrow spacing at first
     * 2. break elements with hypenation.
     * @param textLayoutContext
     * @param element
     * @return
     */
    private boolean onOutOfSpace(final TextLayoutContext textLayoutContext, final Element element) {
        if (textLayoutContext.borrowFromSpacing(element)) {
            textLayoutContext.addElement(element);
            textLayoutContext.averageCurrentLineSpacing();
            return true;
        }
        return breakElement(textLayoutContext, element);
    }

    /**
     * When new line, make sure the first element should not be punctuation, if punctuation found
     * we need to remove the last element from last line and move the element to this line
     * @param textLayoutContext
     * @param lastElement
     * @return
     */
    private boolean onNewLine(final TextLayoutContext textLayoutContext, final Element lastElement) {
        textLayoutContext.averageCurrentLineSpacing();
        final LayoutLine newLayoutLine = textLayoutContext.nextLayoutLine();
        Element currentElement = textLayoutContext.getCurrentElement();
        if (currentElement.canBeLayoutedAtLineBegin()) {
            return true;
        }

        textLayoutContext.adjustPrevLine(newLayoutLine, textLayoutContext.getLimitedRect());
        onLineFinished();
        return false;
    }

    private void onLineFinished() {

    }

    private boolean breakElement(final TextLayoutContext textLayoutContext, final Element element) {
        List<Element> list = element.breakElement(textLayoutContext.getAvailableWidth());
        if (list == null) {
            return false;
        }
        textLayoutContext.replaceElement(element, list);
        textLayoutContext.addElement(list.get(0));
        textLayoutContext.averageCurrentLineSpacing();
        textLayoutContext.nextLayoutLine();
        return true;
    }
}
