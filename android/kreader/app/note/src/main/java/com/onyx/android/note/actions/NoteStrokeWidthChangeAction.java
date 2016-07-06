package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.PenWidthChangeRequest;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteStrokeWidthChangeAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private float strokeWidth;

    public NoteStrokeWidthChangeAction(int sw) {
        this.strokeWidth = sw;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final PenWidthChangeRequest penWidthChangeRequest = new PenWidthChangeRequest(strokeWidth);
        activity.getNoteViewHelper().submit(activity, penWidthChangeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(penWidthChangeRequest, true);
                callback.invoke(callback, request, e);
            }
        });
    }
}
