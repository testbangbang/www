package com.onyx.android.sdk.scribble.request.note;

import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentOpenRequest extends BaseNoteRequest {

    private volatile String documentUniqueId;

    public NoteDocumentOpenRequest(final String id, final String parentUniqueId) {
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
        setParentLibraryId(parentUniqueId);
        documentUniqueId = id;
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        Log.e("#############", "opening document");
        benchmarkStart();
        parent.openDocument(getContext(), documentUniqueId, getParentLibraryId());
        Log.e("#############", "rending page");
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
        Log.e("#############", "document open and render finished :" + benchmarkEnd());
    }

}
