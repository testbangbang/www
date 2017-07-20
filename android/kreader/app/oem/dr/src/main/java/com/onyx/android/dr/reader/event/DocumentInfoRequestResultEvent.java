package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.data.model.Annotation;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huxiaomao on 17/5/17.
 */

public class DocumentInfoRequestResultEvent {
    private ReaderDocumentTableOfContent readerDocumentTableOfContent;
    private List<Bookmark> bookmarks = new ArrayList<>();
    private List<Annotation> anntation = new ArrayList<>();

    public ReaderDocumentTableOfContent getReaderDocumentTableOfContent() {
        return readerDocumentTableOfContent;
    }

    public void setReaderDocumentTableOfContent(ReaderDocumentTableOfContent readerDocumentTableOfContent) {
        this.readerDocumentTableOfContent = readerDocumentTableOfContent;
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(List<Bookmark> bookmarks) {
        this.bookmarks.addAll(bookmarks);
    }

    public void setAnntation(List<Annotation> anntation) {
        this.anntation = anntation;
    }

    public List<Annotation> getAnntation() {
        return anntation;
    }
}
