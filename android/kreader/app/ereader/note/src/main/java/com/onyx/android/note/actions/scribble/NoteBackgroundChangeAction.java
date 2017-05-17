package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.NoteBackgroundChangeRequest;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteBackgroundChangeAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private int backgroundType;
    private NoteBackgroundChangeRequest bgChangeRequest;
    private boolean resume;

    public NoteBackgroundChangeAction(int backgroundType ,boolean resume) {
        this.backgroundType = backgroundType;
        this.resume = resume;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        bgChangeRequest = new NoteBackgroundChangeRequest(backgroundType, resume);
        activity.submitRequest(bgChangeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(bgChangeRequest, true);
                callback.invoke(callback, bgChangeRequest, e);
            }
        });
    }
}
