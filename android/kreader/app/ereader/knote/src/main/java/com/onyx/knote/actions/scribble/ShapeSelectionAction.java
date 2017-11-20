package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeSelectionRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.knote.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/9 11:51.
 */

public class ShapeSelectionAction extends BaseNoteAction {
    private TouchPoint start;
    private TouchPoint end;

    public ShapeSelectionAction(TouchPoint start, TouchPoint end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        ShapeSelectionRequest shapeSelectionRequest = new ShapeSelectionRequest(start, end);
        noteManager.submitRequest(shapeSelectionRequest, callback);
    }
}
