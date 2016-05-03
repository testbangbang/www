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

    public boolean isLoadFinished() {
        return bookModel.getTextModel().isLoadFinished();
    }

    /**
     * check if we have more paragraph loaded in memory.
     * @return
     */
    public boolean hasNextParagraph() {
        if (paragraphCount < 0) {
            return true;
        }
        if (paragraphCount > 0 && currentParagraph < paragraphCount) {
            return true;
        }
        return false;
    }

    private boolean hasNextEntry() {
        final Paragraph paragraph = currentParagraph();
        if (paragraph == null) {
            return false;
        }
        return paragraph.hasNextEntry();
    }

    public boolean hasNext() {
        return (!isLoadFinished() || hasNextEntry() || hasNextParagraph());
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
        ++currentParagraph;
        return lastParagraph;
    }

    public boolean isFirstParagraph() {
        return (currentParagraph == 0);
    }

    public boolean fetchMore() {
        if (reader != null && !reader.processNext(bookModel)) {
            return false;
        }
        update();
        if (reader == null) {
            return false;
        }
        return true;
    }

    private void update() {
        final List<Paragraph> paragraphList = bookModel.getTextModel().getParagraphList();
        if (paragraphList.size() < 0) {
            return;
        }
        paragraphCount = paragraphList.size();
    }

}
