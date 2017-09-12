package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by zhuzeng on 7/12/16.
 */
public class ChangeCurrentShapeRequest extends AsyncBaseNoteRequest {

    private volatile int newShapeType;

    public ChangeCurrentShapeRequest(int type) {
        newShapeType = type;
        setPauseInputProcessor(true);
        setResumeInputProcessor(ShapeFactory.isDFBShape(newShapeType));
    }

    @Override
    public void execute(final NoteManager parent) throws Exception {
        parent.setCurrentShapeType(newShapeType);
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
    }

}
