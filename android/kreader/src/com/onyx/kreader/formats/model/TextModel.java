package com.onyx.kreader.formats.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by zengzhu on 2/28/16.
 */
public class TextModel {

    private List<Paragraph> paragraphList = new ArrayList<Paragraph>();
    private boolean loadFinished = false;

    public void reset() {
        paragraphList.clear();
        setLoadFinished(false);
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

    public boolean isLoadFinished() {
        return loadFinished;
    }

    public void setLoadFinished(boolean finished) {
        loadFinished = finished;
    }

    public int paragraphCount() {
        return paragraphList.size();
    }

    public final List<Paragraph> getParagraphList() {
        return paragraphList;
    }

    public void dump() {

    }


}
