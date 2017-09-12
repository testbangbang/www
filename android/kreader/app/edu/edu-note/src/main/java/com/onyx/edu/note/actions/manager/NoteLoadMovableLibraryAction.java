package com.onyx.edu.note.actions.manager;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.note.NoteLoadMovableLibraryRequest;
import com.onyx.edu.note.actions.BaseNoteAction;

import java.util.List;

/**
 * Created by solskjaer49 on 16/6/28 17:44.
 */

public class NoteLoadMovableLibraryAction extends BaseNoteAction {
    static final String TAG = NoteLoadMovableLibraryAction.class.getSimpleName();

    public NoteLoadMovableLibraryAction(String currentLibID, List<String> excludeIDList) {
        this.currentLibID = currentLibID;
        this.excludeIDList = excludeIDList;
    }

    private List<String> excludeIDList;
    private String currentLibID;

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        NoteLoadMovableLibraryRequest loadMovableLibraryRequest = new NoteLoadMovableLibraryRequest(currentLibID, excludeIDList);
        noteManager.submitRequest(loadMovableLibraryRequest, callback);
    }
}
