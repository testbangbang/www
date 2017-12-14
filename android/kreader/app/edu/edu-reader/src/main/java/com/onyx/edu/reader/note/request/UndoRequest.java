package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNotePage;

/**
 * Created by zhuzeng on 9/23/16.
 */
public class UndoRequest extends ReaderBaseNoteRequest {

    public UndoRequest(final PageInfo p) {
        setVisiblePage(p);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        String pageName = getVisiblePages().get(0).getName();
        boolean isShapeListEmpty = isShapeListEmpty(noteManager, pageName);
        noteManager.undo(getContext(), pageName);
        boolean rendered = renderVisiblePages(noteManager) || !isShapeListEmpty;
        getNoteDataInfo().setContentRendered(rendered);
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

    private boolean isShapeListEmpty(final NoteManager noteManager, String name) {
        ReaderNotePage notePage = noteManager.getReaderNotePage(getContext(), name);
        if (notePage == null) {
            return true;
        }
        return CollectionUtils.isNullOrEmpty(notePage.getShapeList()) &&
                CollectionUtils.isNullOrEmpty(notePage.getReviewShapeList());
    }
}
