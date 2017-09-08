package com.onyx.edu.note.actions.scribble;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteDocumentSaveRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 6/29/16.
 */
public class DocumentSaveAction extends BaseNoteAction {
    private volatile String mDocumentTitle;
    private volatile String mDocumentUniqueId;
    private volatile boolean mCloseAfterSave;

    public DocumentSaveAction(final String documentUniqueId, final String documentTitle, boolean closeAfterSave) {
        mDocumentTitle = documentTitle;
        mDocumentUniqueId = documentUniqueId;
        mCloseAfterSave = closeAfterSave;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        NoteDocumentSaveRequest saveRequest = new NoteDocumentSaveRequest(mDocumentTitle, mCloseAfterSave);
        if (!mCloseAfterSave) {
            saveRequest.setRender(true);
        }
        noteManager.submitRequestWithIdentifier(saveRequest, mDocumentUniqueId, callback);
    }
}
