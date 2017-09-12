package com.onyx.edu.note.actions.manager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteRenameRequest;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 16/7/21 15:24.
 */

public class RenameNoteOrLibraryAction extends BaseNoteAction {
    public RenameNoteOrLibraryAction(String targetId, String newName) {
        this.targetId = targetId;
        this.newName = newName;
    }

    private String targetId;
    private String newName;

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        NoteRenameRequest renameRequest = new NoteRenameRequest(targetId, newName);
        noteManager.submitRequest(renameRequest, callback);
    }
}
