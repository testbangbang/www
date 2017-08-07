package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Simple DrawPage Action,Only To Trigger NoteViewUtil.drawPage() method;
 * Created by solskjaer49 on 2017/7/18 17:32.
 */

public class DrawPageAction extends BaseNoteAction {
    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        AsyncBaseNoteRequest request = new AsyncBaseNoteRequest();
        noteManager.submitRequest(request, callback);
    }
}
