package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.note.NotePageShapesRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/6/30 12:19.
 */

public class NotePageShapeAction extends BaseNoteAction {

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        NotePageShapesRequest notePageShapesRequest = new NotePageShapesRequest(noteManager.getNoteDocument().getCurrentPageUniqueId());
        noteManager.submitRequest(notePageShapesRequest, callback);
    }
}
