package com.onyx.kreader.text;

import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;

import java.util.List;

/**
 * Created by zengzhu on 3/11/16.
 */
public class TextPosition {

    private int currentParagraph;
    private int paragraphCount;

    private int currentEntry = -1;
    private int entryCount;

    private int currentRunOffset;
    private int length;

    private BookModel bookModel;

    public TextPosition(final BookModel parent) {
        bookModel = parent;
    }

    public final String getCurrentText() {
        final Paragraph paragraph = bookModel.getTextModel().getParagraph(currentParagraph);
        if (paragraph == null) {
            return null;
        }

        entryCount = paragraph.getEntryCount();
        final ParagraphEntry entry = paragraph.getEntry(currentEntry);
        if (entry == null) {
            return null;
        }

        if (entry instanceof TextParagraphEntry) {
            TextParagraphEntry textParagraphEntry = (TextParagraphEntry) entry;
            return textParagraphEntry.getText();
        }
        return null;
    }

    public boolean hasNext() {
        return !(isLastParagraph() && isLastEntry());
    }

    public final String next() {
        if (isLastEntry()) {
            return nextParagraph();
        }
        return nextEntry();
    }

    private final String nextParagraph() {
        if (!hasNext()) {
            return null;
        }
        ++currentParagraph;
        currentEntry = 0;
        final Paragraph paragraph = bookModel.getTextModel().getParagraph(currentParagraph);
        entryCount = paragraph.getEntryCount();
        return getCurrentText();
    }

    private final String nextEntry() {
        if (isLastEntry()) {
            return nextParagraph();
        }
        ++currentEntry;
        return getCurrentText();
    }

    public boolean isFirstParagraph() {
        return (currentParagraph == 0);
    }

    public boolean isLastParagraph() {
        return (paragraphCount > 0 && currentParagraph >= paragraphCount - 1);
    }

    public boolean isFirstEntry() {
        return (currentEntry == 0);
    }

    public boolean isLastEntry() {
        return (currentEntry >= entryCount - 1 && entryCount > 0);
    }

    public boolean isLastRun() {
        return (currentRunOffset >= length && length > 0);
    }
}
