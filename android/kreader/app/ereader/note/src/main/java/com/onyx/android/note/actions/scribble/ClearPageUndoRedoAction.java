package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.ClearPageUndoRedoRequest;
import com.onyx.android.sdk.scribble.request.shape.UndoRequest;

/**
 * Created by zhuzeng on 7/16/16.
 */
public class ClearPageUndoRedoAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private ClearPageUndoRedoRequest request;
    private boolean resume;

    public ClearPageUndoRedoAction(boolean resume) {
        this.resume = resume;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(ClearPageUndoRedoAction.this.request, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        request = new ClearPageUndoRedoRequest(resume);
        activity.submitRequest(request, callback);
    }

}
