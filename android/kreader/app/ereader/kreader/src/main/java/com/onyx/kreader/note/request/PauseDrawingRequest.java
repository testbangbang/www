package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 26/12/2016.
 */

public class PauseDrawingRequest extends ReaderBaseNoteRequest {

    public PauseDrawingRequest(final List<PageInfo> list) {
        setVisiblePages(list);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        setResumeRawInputProcessor(false);
    }

}
