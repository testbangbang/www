package com.onyx.android.sdk.scribble.request.note;

import android.text.StaticLayout;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentOpenRequest extends BaseNoteRequest {

    private volatile String documentUniqueId;
    private volatile boolean newCreate;

    public NoteModel getNoteModel() {
        return noteModel;
    }

    private NoteModel noteModel;

    public NoteDocumentOpenRequest(final String id, final String parentUniqueId, boolean create) {
        this(id, parentUniqueId, create,null);
    }

    public NoteDocumentOpenRequest(final String id, final String parentUniqueId, boolean create, String groupId) {
        newCreate = create;
        setParentLibraryId(parentUniqueId);
        setGroupId(groupId);
        documentUniqueId = id;
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        benchmarkStart();
        if (newCreate) {
            parent.createDocument(getContext(), documentUniqueId, getParentLibraryId(), getGroupId(), calculateMinPageCount(parent));
        } else {
            parent.openDocument(getContext(), documentUniqueId, getParentLibraryId(), getGroupId());
        }
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
        setResumeInputProcessor(parent.useDFBForCurrentState());
        noteModel = NoteDataProvider.load(documentUniqueId);
    }

    private int calculateMinPageCount(final NoteViewHelper parent) {
        String text = parent.getDrawText();
        if (StringUtils.isNullOrEmpty(text)) {
            return 1;
        }
        StaticLayout sl = parent.getTextLayout(text, parent.getViewportSize().width());
        return (int) Math.ceil((float)sl.getHeight() / getViewportSize().height());
    }
}
