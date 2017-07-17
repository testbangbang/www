package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
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

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.setCurrentShapeType(newShapeType);
        renderCurrentPage(parent);
        updateShapeDataInfo(parent);
    }

}
