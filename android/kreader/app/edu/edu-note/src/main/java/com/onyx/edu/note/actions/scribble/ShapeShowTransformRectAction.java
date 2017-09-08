package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ShapeShowTransformRectRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/11 17:04.
 */

public class ShapeShowTransformRectAction extends BaseNoteAction {
    private TouchPoint start;
    private TouchPoint end;

    public ShapeShowTransformRectAction(TouchPoint start, TouchPoint end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        ShapeShowTransformRectRequest shapeShowTransformRectRequest = new ShapeShowTransformRectRequest(start, end);
        noteManager.submitRequest(shapeShowTransformRectRequest, callback);
    }
}
