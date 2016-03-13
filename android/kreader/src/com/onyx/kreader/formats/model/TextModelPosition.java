package com.onyx.kreader.formats.model;

import com.onyx.kreader.formats.model.BookModel;
import com.onyx.kreader.formats.model.BookReader;
import com.onyx.kreader.formats.model.Paragraph;
import com.onyx.kreader.formats.model.entry.ParagraphEntry;
import com.onyx.kreader.formats.model.entry.TextParagraphEntry;

import java.util.List;

/**
 * Created by zengzhu on 3/11/16.
 */
public class TextModelPosition {

    private int currentParagraph = 0;
    private int paragraphCount = -1;

    private int currentEntry = 0;
    private int entryCountOfCurrentParagraph;

    private final BookModel bookModel;
    private final BookReader reader;

    public TextModelPosition(final BookReader r, final BookModel parent) {
        bookModel = parent;
        reader = r;
    }

    public final String getCurrentText() {
        final Paragraph paragraph = bookModel.getTextModel().getParagraph(currentParagraph);
        if (paragraph == null) {
            return null;
        }

        entryCountOfCurrentParagraph = paragraph.getEntryCount();
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
        final String current = getCurrentText();
        if (isLastEntry()) {
            nextParagraph();
        } else {
            nextEntry();
        }
        return current;
    }

    private boolean nextParagraph() {
        if (!hasNext()) {
            return false;
        }
        ++currentParagraph;
        currentEntry = 0;
        final Paragraph paragraph = bookModel.getTextModel().getParagraph(currentParagraph);
        if (paragraph == null) {
            return false;
        }
        entryCountOfCurrentParagraph = paragraph.getEntryCount();
        return true;
    }

    private boolean nextEntry() {
        if (!hasNext()) {
            return false;
        }
        if (isLastEntry()) {
            return nextParagraph();
        }
        ++currentEntry;
        return true;
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
        return (currentEntry >= entryCountOfCurrentParagraph - 1 && entryCountOfCurrentParagraph > 0);
    }

    /**
     * get available text length in model that has been loaded.
     * @return
     */
    public int availableTextLength() {
        int available = 0;
        final List<Paragraph> paragraphList = bookModel.getTextModel().getParagraphList();
        if (paragraphList.size() <= 0) {
            return available;
        }
        Paragraph paragraph = paragraphList.get(currentParagraph);
        available += paragraph.availableTextLength(currentEntry);

        for(int i = currentParagraph + 1; i < paragraphList.size(); ++i) {
            paragraph = paragraphList.get(i);
            available += paragraph.availableTextLength(0);
        }
        return available;
    }

    public boolean fetchMore() {
        if (!reader.processNext(bookModel)) {
            return false;
        }
        update();
        return true;
    }

    private void update() {
        final List<Paragraph> paragraphList = bookModel.getTextModel().getParagraphList();
        if (paragraphList.size() < 0) {
            return;
        }
        paragraphCount = paragraphList.size();
        entryCountOfCurrentParagraph = paragraphList.get(currentParagraph).getEntryList().size();
    }

}
