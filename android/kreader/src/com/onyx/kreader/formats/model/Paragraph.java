package com.onyx.kreader.formats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/28/16.
 */
public class Paragraph {

    static public enum ParagraphKind {
        TEXT_PARAGRAPH,
        TREE_PARAGRAPH,
        EMPTY_LINE_PARAGRAPH,
        BEFORE_SKIP_PARAGRAPH,
        AFTER_SKIP_PARAGRAPH,
        END_OF_SECTION_PARAGRAPH,
        PSEUDO_END_OF_SECTION_PARAGRAPH,
        END_OF_TEXT_PARAGRAPH,
        ENCRYPTED_SECTION_PARAGRAPH,
    }


    static public Paragraph create(final ParagraphKind kind) {
        Paragraph paragraph = new Paragraph(kind);
        return paragraph;
    }


    private long streamPosition;
    private long streamLength;
    private ParagraphKind paragraphKind;
    private List<ParagraphEntry> paragraphEntryList = new ArrayList<ParagraphEntry>();

    public Paragraph(final ParagraphKind kind) {
        paragraphKind = kind;
    }

    public void addEntry(final ParagraphEntry paragraphEntry) {
        paragraphEntryList.add(paragraphEntry);
    }

    public final List<ParagraphEntry> getParagraphEntryList() {
        return paragraphEntryList;
    }

}
