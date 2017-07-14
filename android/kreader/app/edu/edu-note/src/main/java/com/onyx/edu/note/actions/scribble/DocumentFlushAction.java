package com.onyx.edu.note.actions.scribble;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.request.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.note.NoteManager;
import com.onyx.edu.note.actions.BaseNoteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class DocumentFlushAction extends BaseNoteAction {

    private volatile List<Shape> shapeList = new ArrayList<>();
    private volatile boolean resumeDrawing;
    private volatile boolean render;
    private volatile NoteDrawingArgs drawingArgs;
    private volatile boolean mForceRender;

    public DocumentFlushAction(final List<Shape> list, boolean r, boolean resume, boolean forceRender, final NoteDrawingArgs args) {
        if (list != null) {
            shapeList.addAll(list);
        }
        render = r;
        resumeDrawing = resume;
        drawingArgs = args;
        mForceRender = forceRender;
    }

    @Override
    public void execute(NoteManager noteManager, BaseCallback callback) {
        PageFlushRequest flushRequest = new PageFlushRequest(shapeList, render, resumeDrawing, drawingArgs);
        if (mForceRender) {
            flushRequest.setRender(mForceRender);
        }
        noteManager.submitRequest(flushRequest, callback);
    }
}
