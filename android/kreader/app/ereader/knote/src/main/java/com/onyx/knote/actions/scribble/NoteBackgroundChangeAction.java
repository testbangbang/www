package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.shape.NoteBackgroundChangeRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteBackgroundChangeAction extends BaseNoteAction {
    private int backgroundType;
    private boolean resume;

    public NoteBackgroundChangeAction(int backgroundType, boolean resume) {
        this.backgroundType = backgroundType;
        this.resume = resume;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        noteManager.getShapeDataInfo().setBackground(backgroundType);
        NoteBackgroundChangeRequest bgChangeRequest = new NoteBackgroundChangeRequest(backgroundType, resume);
        bgChangeRequest.setRender(true);
        noteManager.submitRequest(bgChangeRequest, callback);
    }
}
