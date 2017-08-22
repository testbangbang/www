package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.edu.reader.note.NoteManager;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class ClearPageRequest extends ReaderBaseNoteRequest {

    private volatile PageInfo pageInfo;
    public ClearPageRequest(final PageInfo p) {
        pageInfo = p;
        setVisiblePage(p);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        if (noteManager.getParent().getHandlerManager().lockShapeByDocumentStatus() && noteManager.getNoteDocument().isLock()) {
            return;
        }
        boolean lockShapeByRevision = noteManager.getParent().getHandlerManager().lockShapeByRevision();
        noteManager.getNoteDocument().clearPage(getContext(), pageInfo.getName(), 0, lockShapeByRevision);
        renderVisiblePages(noteManager);
        getNoteDataInfo().setContentRendered(true);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }
}
