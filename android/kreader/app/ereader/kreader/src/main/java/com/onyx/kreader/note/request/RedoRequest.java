package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.note.NoteManager;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class RedoRequest extends ReaderBaseNoteRequest {

    public RedoRequest(final PageInfo p) {
        setRender(true);
        setTransfer(true);
        setVisiblePage(p);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.redo(getContext(), getVisiblePages().get(0).getName(),
                getVisiblePages().get(0).getSubPage());
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
