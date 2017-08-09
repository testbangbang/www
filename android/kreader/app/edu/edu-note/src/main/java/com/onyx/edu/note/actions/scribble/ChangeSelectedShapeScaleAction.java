package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.asyncrequest.shape.ChangedSelectedShapeScaleRequest;
import com.onyx.edu.note.actions.BaseNoteAction;

/**
 * Created by solskjaer49 on 2017/8/9 18:09.
 */

public class ChangeSelectedShapeScaleAction extends BaseNoteAction {

    public ChangeSelectedShapeScaleAction(float scale) {
        this.scaleSize = scale;
    }

    private float scaleSize;

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        ChangedSelectedShapeScaleRequest request = new ChangedSelectedShapeScaleRequest(scaleSize);
        noteManager.submitRequest(request, callback);
    }
}
