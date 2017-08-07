package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentOpenRequest extends AsyncBaseNoteRequest {

    private volatile String documentUniqueId;
    private volatile boolean newCreate;

    public NoteModel getNoteModel() {
        return noteModel;
    }

    private NoteModel noteModel;

    public NoteDocumentOpenRequest(final String id, final String parentUniqueId, boolean create) {
        newCreate = create;
        setParentLibraryId(parentUniqueId);
        documentUniqueId = id;
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        benchmarkStart();
        if (newCreate) {
            parent.createDocument(getContext(), documentUniqueId, getParentLibraryId());
        } else {
            parent.openDocument(getContext(), documentUniqueId, getParentLibraryId());
        }
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
        setResumeInputProcessor(parent.useDFBForCurrentState());
        noteModel = NoteDataProvider.load(documentUniqueId);
    }

}
