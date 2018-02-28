package com.onyx.android.note.action;

import com.onyx.android.note.common.base.BaseNoteAction;
import com.onyx.android.sdk.note.NoteManager;
import com.onyx.android.sdk.note.request.SaveDocumentRequest;
import com.onyx.android.sdk.rx.RxCallback;

/**
 * Created by lxm on 2018/2/25.
 */

public class SaveDocumentAction extends BaseNoteAction {

    private boolean closeAfterSave;
    private String title;

    public SaveDocumentAction(NoteManager noteManager) {
        super(noteManager);
    }

    public SaveDocumentAction setTitle(String title) {
        this.title = title;
        return this;
    }

    public SaveDocumentAction setCloseAfterSave(boolean closeAfterSave) {
        this.closeAfterSave = closeAfterSave;
        return this;
    }

    @Override
    public void execute(RxCallback rxCallback) {
        SaveDocumentRequest request = new SaveDocumentRequest(getNoteManager())
                .setCloseAfterSave(closeAfterSave)
                .setTitle(title);
        getNoteManager().getRxManager().enqueue(request, rxCallback);
    }
}
