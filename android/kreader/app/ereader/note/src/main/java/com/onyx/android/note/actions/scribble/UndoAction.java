package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.UndoRequest;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class UndoAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private UndoRequest undoRequest;
    private boolean resumeDrawing;

    public UndoAction(boolean resumeDrawing) {
        this.resumeDrawing = resumeDrawing;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(undoRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        undoRequest = new UndoRequest(resumeDrawing);
        activity.submitRequest(undoRequest, callback);
    }

}
