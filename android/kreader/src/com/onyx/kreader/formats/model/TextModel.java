package com.onyx.kreader.formats.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TextModel {

    private List<Paragraph> paragraphList = new ArrayList<Paragraph>();

    public Paragraph createParagraph(final Paragraph.ParagraphKind paragraphKind) {
        Paragraph paragraph = new Paragraph(paragraphKind) ;
        return paragraph;
    }

    public void addParagraph(final Paragraph paragraph) {
        paragraphList.add(paragraph);
    }

    public Paragraph getLastParagraph() {
        if (paragraphList.size() <= 0) {
            return null;
        }
        return paragraphList.get(paragraphList.size() - 1);
    }

    public void dump() {

    }


}
