package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentOpenRequest extends BaseNoteRequest {

    private volatile String documentUniqueId;
    private volatile boolean newCreate;
    private boolean resume;

    public NoteModel getNoteModel() {
        return noteModel;
    }

    private NoteModel noteModel;

    public NoteDocumentOpenRequest(final String id, final String parentUniqueId, boolean create, boolean r) {
        this(id, parentUniqueId, create, r, null);
    }

    public NoteDocumentOpenRequest(final String id, final String parentUniqueId, boolean create, boolean r, String groupId) {
        newCreate = create;
        setParentLibraryId(parentUniqueId);
        setGroupId(groupId);
        documentUniqueId = id;
        resume = r;
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        benchmarkStart();
        if (newCreate) {
            parent.createDocument(getContext(), documentUniqueId, getParentLibraryId(), getGroupId());
        } else {
            parent.openDocument(getContext(), documentUniqueId, getParentLibraryId(), getGroupId());
        }
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
        setResumeInputProcessor(parent.useDFBForCurrentState() && resume);
        noteModel = NoteDataProvider.load(documentUniqueId);
    }

}
