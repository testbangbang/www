package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.request.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class DocumentFlushAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private volatile List<Shape> shapeList = new ArrayList<>();
    private volatile boolean resumeDrawing;
    private volatile boolean render;
    private volatile NoteDrawingArgs drawingArgs;
    private PageFlushRequest flushRequest;

    public DocumentFlushAction(final List<Shape> list, boolean r, boolean resume, final NoteDrawingArgs args) {
        if (list != null) {
            shapeList.addAll(list);
        }
        render = r;
        resumeDrawing = resume;
        drawingArgs = args;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        flushRequest = new PageFlushRequest(shapeList, render, resumeDrawing, drawingArgs);
        activity.submitRequest(flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(flushRequest, true);
                callback.invoke(callback, request, e);
            }
        });
    }
}
