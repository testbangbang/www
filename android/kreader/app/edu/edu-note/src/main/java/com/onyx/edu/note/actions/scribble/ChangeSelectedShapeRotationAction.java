package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ChangeSelectedShapeRotationRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/9 18:09.
 */

public class ChangeSelectedShapeRotationAction extends BaseNoteAction {

    private float targetRotationAngle = Float.MIN_VALUE;
    private TouchPoint touchPoint;
    private volatile boolean isAddToHistory = false;

    public ChangeSelectedShapeRotationAction(float targetAngle, boolean isAddToHistory) {
        this.targetRotationAngle = targetAngle;
        this.isAddToHistory = isAddToHistory;
    }

    public ChangeSelectedShapeRotationAction(TouchPoint touchPoint, boolean isAddToHistory) {
        this.touchPoint = touchPoint;
        this.isAddToHistory = isAddToHistory;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        ChangeSelectedShapeRotationRequest request;
        if ((Float.compare(targetRotationAngle, Float.MIN_VALUE) == 0) && (touchPoint != null)) {
            request = new ChangeSelectedShapeRotationRequest(touchPoint,isAddToHistory);
        } else {
            request = new ChangeSelectedShapeRotationRequest(targetRotationAngle,isAddToHistory);
        }
        noteManager.submitRequest(request, callback);
    }
}
