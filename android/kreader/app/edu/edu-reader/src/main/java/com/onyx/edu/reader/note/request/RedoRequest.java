package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.edu.reader.note.NoteManager;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class RedoRequest extends ReaderBaseNoteRequest {

    public RedoRequest(final PageInfo p) {
        setVisiblePage(p);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.redo(getContext(), getVisiblePages().get(0).getName());
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
