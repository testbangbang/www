package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ChangeSelectedShapeScaleRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/9 18:09.
 */

public class ChangeSelectedShapeScaleAction extends BaseNoteAction {
    private volatile TouchPoint touchPoint = null;
    private volatile float scaleSize = Float.MIN_VALUE;

    public ChangeSelectedShapeScaleAction(TouchPoint touchPoint, boolean isAddToHistory) {
        this.touchPoint = touchPoint;
        this.isAddToHistory = isAddToHistory;
    }

    public ChangeSelectedShapeScaleAction(float scaleSize, boolean isAddToHistory) {
        this.scaleSize = scaleSize;
        this.isAddToHistory = isAddToHistory;
    }

    private volatile boolean isAddToHistory = false;

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        ChangeSelectedShapeScaleRequest request;
        if (Float.compare(scaleSize, Float.MIN_VALUE) == 0 && (touchPoint != null)) {
            request = new ChangeSelectedShapeScaleRequest(touchPoint, isAddToHistory);
        } else {
            request = new ChangeSelectedShapeScaleRequest(scaleSize, isAddToHistory);
        }
        noteManager.submitRequest(request, callback);
    }
}
