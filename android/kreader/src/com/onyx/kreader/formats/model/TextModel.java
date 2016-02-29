package com.onyx.kreader.formats.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TextModel {

    private List<Paragraph> paragraphList = new ArrayList<Paragraph>();

    public Paragraph createParagraph() {
        return Paragraph.create();
    }

    
}
