package com.onyx.edu.note.actions.manager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteLibraryCreateRequest;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by zhuzeng on 6/26/16.
 */
public class CreateLibraryAction extends BaseNoteAction {
    private volatile String parentLibraryId;
    private volatile String title;

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        final NoteLibraryCreateRequest createRequest = new NoteLibraryCreateRequest(parentLibraryId, title);
        createRequest.setRender(false);
        noteManager.submitRequest(createRequest, callback);
    }

    public CreateLibraryAction(final String id, final String t) {
        parentLibraryId = id;
        title = t;
    }
}
