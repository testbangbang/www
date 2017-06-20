package com.onyx.android.sdk.reader.api;

import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhuzeng on 10/3/15.
 */
public class ReaderDocumentTableOfContentEntry {

    private String title;
    private String pageName;
    private String position;
    private List<ReaderDocumentTableOfContentEntry> children;

    public ReaderDocumentTableOfContentEntry() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public String getPageName() {
        return pageName;
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
        entry.pageName = PagePositionUtils.fromPageNumber(pageNumber);
        entry.position = entry.pageName;
        return entry;
    }

    public static ReaderDocumentTableOfContentEntry createEntry(final String title, int pageNumber, String position) {
        ReaderDocumentTableOfContentEntry entry = new ReaderDocumentTableOfContentEntry();
        entry.title = title;
        entry.pageName = PagePositionUtils.fromPageNumber(pageNumber);
        entry.position = position;
        return entry;
    }

    @SuppressWarnings("unused")
    static public ReaderDocumentTableOfContentEntry addEntry(ReaderDocumentTableOfContentEntry parent, final String title, int pageNumber) {
        return addEntry(parent, createEntry(title, pageNumber));
    }

    static public ReaderDocumentTableOfContentEntry addEntry(ReaderDocumentTableOfContentEntry parent, final String title, int pageNumber, String position) {
        return addEntry(parent, createEntry(title, pageNumber, position));
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
