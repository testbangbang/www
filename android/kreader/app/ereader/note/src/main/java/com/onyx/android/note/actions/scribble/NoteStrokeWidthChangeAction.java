package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.PenWidthChangeRequest;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteStrokeWidthChangeAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private PenWidthChangeRequest penWidthChangeRequest;
    private float strokeWidth;

    public NoteStrokeWidthChangeAction(int sw) {
        this.strokeWidth = sw;
    }


    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(penWidthChangeRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        penWidthChangeRequest = new PenWidthChangeRequest(strokeWidth);
        activity.submitRequest(penWidthChangeRequest, callback);
    }
}
