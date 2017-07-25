package com.onyx.edu.note.actions.scribble;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.NoteLineLayoutBackgroundChangeRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by ming on 16/12/19 16:10.
 */

public class NoteLineLayoutBackgroundChangeAction extends BaseNoteAction {
    private int backgroundType;
    private boolean resume;

    public NoteLineLayoutBackgroundChangeAction(int backgroundType, boolean resume) {
        this.backgroundType = backgroundType;
        this.resume = resume;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        noteManager.getShapeDataInfo().setLineLayoutBackground(backgroundType);
        NoteLineLayoutBackgroundChangeRequest bgChangeRequest = new NoteLineLayoutBackgroundChangeRequest(backgroundType, resume);
        noteManager.submitRequest(bgChangeRequest, callback);
    }
}
