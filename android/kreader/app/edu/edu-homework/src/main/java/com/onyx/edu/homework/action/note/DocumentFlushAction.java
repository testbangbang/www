package com.onyx.edu.homework.action.note;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.request.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.homework.base.BaseNoteAction;
import com.onyx.edu.homework.event.RequestFinishedEvent;

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

    public DocumentFlushAction(final List<Shape> list, boolean r, boolean resume, final NoteDrawingArgs args) {
        if (list != null) {
            shapeList.addAll(list);
        }
        render = r;
        resumeDrawing = resume;
        drawingArgs = args;
    }

    @Override
    public void execute(final NoteViewHelper noteViewHelper, final BaseCallback baseCallback) {
        final PageFlushRequest flushRequest = new PageFlushRequest(shapeList, render, resumeDrawing, drawingArgs);
        noteViewHelper.submit(getAppContext(), flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                post(RequestFinishedEvent.create(flushRequest, e, true));
                BaseCallback.invoke(baseCallback, request, e);
            }
        });
    }
}
