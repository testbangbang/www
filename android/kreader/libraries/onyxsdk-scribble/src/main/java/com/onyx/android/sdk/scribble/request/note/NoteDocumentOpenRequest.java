package com.onyx.android.sdk.scribble.request.note;

import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentOpenRequest extends BaseNoteRequest {

    private volatile String documentUniqueId;
    private volatile boolean newCreate;

    public NoteDocumentOpenRequest(final String id, final String parentUniqueId, boolean create) {
        newCreate = create;
        setParentLibraryId(parentUniqueId);
        documentUniqueId = id;
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        Log.e("#############", "opening document");
        benchmarkStart();
        if (newCreate) {
            parent.createDocument(getContext(), documentUniqueId, getParentLibraryId());
        } else {
            parent.openDocument(getContext(), documentUniqueId, getParentLibraryId());
        }
        Log.e("#############", "rending page");
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
        Log.e("#############", "document open and render finished :" + benchmarkEnd());
    }

}
