package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 3/11/16.
 */
public class TextPosition {


    private int currentParagraph;
    private int paragraphCount;

    private int currentEntry;
    private int entryCount;

    private int currentRunOffset;
    private int length;


    public TextPosition() {

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
        return (currentEntry >= entryCount && entryCount > 0);
    }

    public boolean isLastRun() {
        return (currentRunOffset >= length && length > 0);
    }
}
