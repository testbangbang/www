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

    public final Paragraph currentParagraph() {
        final Paragraph paragraph = bookModel.getTextModel().getParagraph(currentParagraph);
        if (paragraph == null) {
            return null;
        }
        return paragraph;
    }

    public final ParagraphEntry currentParagraphEntry() {
        final Paragraph paragraph = currentParagraph();
        if (paragraph == null) {
            return null;
        }
        return paragraph.getEntry(currentEntry);
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

    public boolean isLoadFinished() {
        return bookModel.getTextModel().isLoadFinished();
    }

    /**
     * check if we have more paragraph loaded in memory.
     * @return
     */
    public boolean hasNextParagraph() {
        return !(isLastParagraph());
    }

    public boolean hasNextEntry() {
        return !(isLastEntry());
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

    /**
     * return current paragraph and move paragraph index to next.
     * @return null if no more paragraph.
     */
    public final Paragraph nextParagraph() {
        if (!hasNextParagraph()) {
            return null;
        }
        final Paragraph lastParagraph = currentParagraph();
        nextParagraphImpl();
        return lastParagraph;
    }

    private final Paragraph nextParagraphImpl() {
        if (!hasNextParagraph()) {
            return null;
        }

        ++currentParagraph;
        currentEntry = 0;
        final Paragraph paragraph = bookModel.getTextModel().getParagraph(currentParagraph);
        entryCountOfCurrentParagraph = paragraph.getEntryCount();
        return paragraph;
    }

    /**
     * return next entry in this paragraph.
     * @return null if no more entry in this paragraph.
     */
    public final ParagraphEntry nextEntry() {
        if (!hasNextEntry()) {
            return null;
        }
        final ParagraphEntry paragraphEntry = currentParagraphEntry();
        nextEntryImpl();
        return paragraphEntry;
    }

    public final ParagraphEntry nextEntryImpl() {
        if (!hasNextEntry()) {
            return null;
        }

        ++currentEntry;
        return currentParagraphEntry();
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
