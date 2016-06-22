package com.onyx.kreader.formats.model.entry;

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

    public final Character nextCharacter(int i) {
        if (i < 0) {
            return text.charAt(0);
        }
        if (i + 1 < text.length()) {
            return text.charAt(i + 1);
        }
        return null;
    }

}
