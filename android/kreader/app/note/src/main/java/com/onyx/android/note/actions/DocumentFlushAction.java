package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class DocumentFlushAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile List<Shape> shapeList;
    private volatile boolean resumeDrawing;
    private volatile boolean render;

    public DocumentFlushAction(final List<Shape> list, boolean r, boolean resume) {
        shapeList = list;
        render = r;
        resumeDrawing = resume;
    }

    public void execute(final T activity, final BaseCallback callback) {
        final PageFlushRequest flushRequest = new PageFlushRequest(shapeList, render, resumeDrawing);
        activity.getNoteViewHelper().submit(activity, flushRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(flushRequest, true);
                if (callback != null) {
                    callback.done(request, e);
                }
            }
        });
    }
}
