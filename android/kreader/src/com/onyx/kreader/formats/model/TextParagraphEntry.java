package com.onyx.kreader.formats.model;

/**
 * Created by zengzhu on 3/1/16.
 */
public class TextParagraphEntry extends ParagraphEntry {

    private String text;


    public TextParagraphEntry(final String string) {
        entryKind = EntryKind.TEXT_ENTRY;
        text = string;
    }

    public final String getText() {
        return text;
    }

}
