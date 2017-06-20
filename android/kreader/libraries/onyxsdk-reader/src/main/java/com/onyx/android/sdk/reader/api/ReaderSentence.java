package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by jim on 6/26/15.
 * javap -s com.onyx.reader.ReaderSentence
 */
public class ReaderSentence {

    private ReaderSelection readerSelection;
    private String nextPosition;
    private boolean endOfScreen;
    private boolean endOfDocument;

    public static ReaderSentence create(ReaderSelection selection, int nextPosition, boolean endOfScreen, boolean endOfDocument) {
        return create(selection, String.valueOf(nextPosition), endOfScreen, endOfDocument);
    }

    public static ReaderSentence create(ReaderSelection selection, String nextPosition, boolean endOfScreen, boolean endOfDocument) {
        ReaderSentence result = new ReaderSentence();
        result.setReaderSelection(selection);
        result.setNextPosition(nextPosition);
        result.setEndOfScreen(endOfScreen);
        result.setEndOfDocument(endOfDocument);
        return result;
    }

    public void setReaderSelection(ReaderSelection readerSelection) {
        this.readerSelection = readerSelection;
    }

    public ReaderSelection getReaderSelection() {
        return readerSelection;
    }

    public String getNextPosition() {
        return nextPosition;
    }

    public void setNextPosition(String nextPosition) {
        this.nextPosition = nextPosition;
    }

    public boolean isNonBlank() {
        return readerSelection != null &&
                readerSelection.getRectangles() != null &&
                readerSelection.getRectangles().size() > 0 &&
                StringUtils.isNotBlank(readerSelection.getText());
    }

    public boolean isEndOfScreen() {
        return endOfScreen;
    }

    public void setEndOfScreen(boolean endOfScreen) {
        this.endOfScreen = endOfScreen;
    }

    public boolean isEndOfDocument() {
        return endOfDocument;
    }

    public void reset() {
        endOfDocument = false;
        endOfScreen = false;
    }

    public void setEndOfDocument(boolean endOfDocument) {
        this.endOfDocument = endOfDocument;
    }

    public void copy(final ReaderSentence result) {
        if (result != null) {
            setReaderSelection(result.getReaderSelection());
            setNextPosition(result.getNextPosition());
            setEndOfScreen(result.endOfScreen);
            setEndOfDocument(result.endOfDocument);
        }
    }

    public void clear() {
        if (getReaderSelection() != null && getReaderSelection().getRectangles() != null) {
            getReaderSelection().getRectangles().clear();
        }
        nextPosition = "";
        reset();
    }
}
