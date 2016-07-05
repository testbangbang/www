package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.NoteBackgroundChangeRequest;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteBackgroundChangeAction<T extends ScribbleActivity> extends BaseNoteAction<T> {
    int backgroundType;

    public NoteBackgroundChangeAction(int backgroundType) {
        this.backgroundType = backgroundType;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteBackgroundChangeRequest bgChangeRequest = new NoteBackgroundChangeRequest(backgroundType);
        activity.getNoteViewHelper().submit(activity, bgChangeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                callback.invoke(callback, request, e);
            }
        });
    }
}
