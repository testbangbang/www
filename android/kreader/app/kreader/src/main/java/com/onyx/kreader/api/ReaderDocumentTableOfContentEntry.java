package com.onyx.kreader.api;

import android.util.Log;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public class ReaderDocumentTableOfContentEntry {

    private String title;
    private String position;
    private List<ReaderDocumentTableOfContentEntry> children;

    public ReaderDocumentTableOfContentEntry() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public String getPosition() {
        return position;
    }

    public List<ReaderDocumentTableOfContentEntry> getChildren() {
        return children;
    }

    @SuppressWarnings("unused")
    public static ReaderDocumentTableOfContentEntry createEntry(final String title, int pageNumber) {
        ReaderDocumentTableOfContentEntry entry = new ReaderDocumentTableOfContentEntry();
        entry.title = title;
        entry.position = PagePositionUtils.fromPosition(pageNumber);
        return entry;
    }

    @SuppressWarnings("unused")
    static public ReaderDocumentTableOfContentEntry addEntry(ReaderDocumentTableOfContentEntry parent, final String title, int pageNumber) {
        return addEntry(parent, createEntry(title, pageNumber));
    }

    @SuppressWarnings("unused")
    static public ReaderDocumentTableOfContentEntry addEntry(ReaderDocumentTableOfContentEntry parent, ReaderDocumentTableOfContentEntry child) {
        if (parent != null) {
            parent.addChildEntry(child);
        }
        return child;
    }

    public void addChildEntry(final ReaderDocumentTableOfContentEntry entry) {
        if (children == null) {
            children = new LinkedList<>();
        }
        children.add(entry);
    }

}
