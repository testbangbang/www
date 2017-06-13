package com.onyx.edu.reader.ui.data;

import com.onyx.edu.reader.note.data.ReaderNotePageNameMap;
import com.onyx.edu.reader.note.model.ReaderFormShapeModel;
import com.onyx.edu.reader.note.model.ReaderNoteDocumentModel;

import java.util.List;
import java.util.Map;

/**
 * Created by ming on 2017/6/10.
 */

public class ReviewDocumentData {

    private String md5;
    private List<ReaderNoteDocumentModel> ReaderNoteDocuments;
    private List<ReaderFormShapeModel> ReaderFormShapes;

    public ReviewDocumentData() {
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public List<ReaderNoteDocumentModel> getReaderNoteDocuments() {
        return ReaderNoteDocuments;
    }

    public void setReaderNoteDocuments(List<ReaderNoteDocumentModel> readerNoteDocuments) {
        ReaderNoteDocuments = readerNoteDocuments;
    }

    public List<ReaderFormShapeModel> getReaderFormShapes() {
        return ReaderFormShapes;
    }

    public void setReaderFormShapes(List<ReaderFormShapeModel> readerFormShapes) {
        ReaderFormShapes = readerFormShapes;
    }

    public ReaderNotePageNameMap getReaderNotePageNameMap() {
        if (ReaderNoteDocuments == null || ReaderNoteDocuments.size() == 0) {
            return null;
        }
        return ReaderNoteDocuments.get(0).getReaderNotePageNameMap();
    }
}
