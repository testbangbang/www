package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.RedoRequest;

/**
 * Created by zhuzeng on 7/19/16.
 */
public class RedoAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private RedoRequest redoRequest;

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(redoRequest, true);
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        redoRequest = new RedoRequest();
        activity.submitRequest(redoRequest, callback);
    }

}
