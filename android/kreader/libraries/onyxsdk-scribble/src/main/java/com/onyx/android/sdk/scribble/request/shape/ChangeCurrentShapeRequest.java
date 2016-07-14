package com.onyx.android.sdk.scribble.request.shape;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;

/**
 * Created by zhuzeng on 7/12/16.
 */
public class ChangeCurrentShapeRequest extends BaseNoteRequest {

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
