package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.edu.reader.note.NoteManager;

/**
 * Created by zhuzeng on 9/28/16.
 */

public class ClearPageRequest extends ReaderBaseNoteRequest {

    private volatile PageInfo pageInfo;
    private volatile boolean lockShapeByDocumentStatus;

    public ClearPageRequest(final PageInfo p, boolean lockShapeByDocumentStatus) {
        pageInfo = p;
        this.lockShapeByDocumentStatus = lockShapeByDocumentStatus;
        setVisiblePage(p);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        if (lockShapeByDocumentStatus && noteManager.getNoteDocument().isLock()) {
            return;
        }
        boolean lockShapeByRevision = noteManager.getParent().getHandlerManager().lockShapeByRevision();
        noteManager.getNoteDocument().clearPage(getContext(), pageInfo.getName(), 0, lockShapeByRevision);
        renderVisiblePages(noteManager);
        getNoteDataInfo().setContentRendered(true);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }
}
