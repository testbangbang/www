package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 10/12/16.
 */

public class ResumeDrawingRequest extends ReaderBaseNoteRequest {

    public ResumeDrawingRequest(final List<PageInfo> list) {
        setVisiblePages(list);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
