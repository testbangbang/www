package com.onyx.knote.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.knote.actions.BaseNoteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class DocumentFlushAction extends BaseNoteAction {

    private volatile List<Shape> shapeList = new ArrayList<>();
    private volatile boolean resumeDrawing;
    private volatile NoteDrawingArgs drawingArgs;
    private volatile boolean mRender;

    public DocumentFlushAction(final List<Shape> list, boolean render, boolean resume, final NoteDrawingArgs args) {
        if (list != null) {
            shapeList.addAll(list);
        }
        resumeDrawing = resume;
        drawingArgs = args;
        mRender = render;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        PageFlushRequest flushRequest = new PageFlushRequest(shapeList, mRender, resumeDrawing, drawingArgs);
        noteManager.submitRequest(flushRequest, callback);
    }
}
