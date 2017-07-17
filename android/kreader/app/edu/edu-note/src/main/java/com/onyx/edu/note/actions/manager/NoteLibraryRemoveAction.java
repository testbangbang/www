package com.onyx.edu.note.actions.manager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteLibraryRemoveRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/27 19:35.
 */

public class NoteLibraryRemoveAction extends BaseNoteAction {
    private List<String> targetRemoveLibraryList;

    public NoteLibraryRemoveAction(List<String> targetRemoveLibraryList) {
        this.targetRemoveLibraryList = targetRemoveLibraryList;
    }

    @Override
    public void execute(NoteManager manager, final BaseCallback callback) {
        final NoteLibraryRemoveRequest request = new NoteLibraryRemoveRequest(targetRemoveLibraryList);
        request.setRender(false);
        manager.submitRequest(request, callback);
    }
}
