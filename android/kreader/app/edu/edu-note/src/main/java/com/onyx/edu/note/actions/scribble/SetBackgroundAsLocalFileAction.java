package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.NoteSetBackgroundAsLocalFileRequest;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class SetBackgroundAsLocalFileAction extends BaseNoteAction {
    private String backgroundFilePath;
    private boolean resume;

    public SetBackgroundAsLocalFileAction(String filePath, boolean resume) {
        this.backgroundFilePath = filePath;
        this.resume = resume;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        NoteSetBackgroundAsLocalFileRequest bgChangeRequest =
                new NoteSetBackgroundAsLocalFileRequest(backgroundFilePath, resume);
        noteManager.submitRequest(bgChangeRequest, callback);
    }
}
