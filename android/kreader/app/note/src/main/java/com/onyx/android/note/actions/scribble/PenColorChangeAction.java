package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.PenColorChangeRequest;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class PenColorChangeAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private int penColor;
    private PenColorChangeRequest penColorChangeRequest;
    private boolean resume;

    public PenColorChangeAction(int color , boolean resume) {
        this.penColor = color;
        this.resume = resume;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        penColorChangeRequest = new PenColorChangeRequest(penColor, resume);
        activity.submitRequest(penColorChangeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(penColorChangeRequest, true);
                callback.invoke(callback, penColorChangeRequest, e);
            }
        });
    }
}
