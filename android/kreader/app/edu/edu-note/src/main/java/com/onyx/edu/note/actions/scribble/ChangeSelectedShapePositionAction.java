package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ChangeSelectedShapePositionRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/9 18:09.
 */

public class ChangeSelectedShapePositionAction extends BaseNoteAction {

    public ChangeSelectedShapePositionAction(float dx, float dy) {
        targetDx = dx;
        targetDy = dy;
    }

    public ChangeSelectedShapePositionAction(TouchPoint targetPoint) {
        touchPoint = targetPoint;
    }

    private float targetDx = Float.MIN_VALUE;
    private float targetDy = Float.MIN_VALUE;
    private TouchPoint touchPoint;

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        ChangeSelectedShapePositionRequest request;
        if ((Float.compare(targetDx, Float.MIN_VALUE) == 0 || Float.compare(targetDy, Float.MIN_VALUE) == 0) && (touchPoint != null)) {
            request = new ChangeSelectedShapePositionRequest(touchPoint);
        } else {
            request = new ChangeSelectedShapePositionRequest(targetDx, targetDy);
        }
        noteManager.submitRequest(request, callback);
    }
}
