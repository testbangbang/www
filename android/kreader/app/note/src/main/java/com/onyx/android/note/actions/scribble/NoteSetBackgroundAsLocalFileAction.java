package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.shape.NoteSetBackgroundAsLocalFileRequest;

/**
 * Created by solskjaer49 on 16/7/5 16:10.
 */

public class NoteSetBackgroundAsLocalFileAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {
    private String backgroundFilePath;
    private NoteSetBackgroundAsLocalFileRequest bgChangeRequest;
    private boolean resume;

    public NoteSetBackgroundAsLocalFileAction(String filePath , boolean resume) {
        this.backgroundFilePath = filePath;
        this.resume = resume;
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        bgChangeRequest = new NoteSetBackgroundAsLocalFileRequest(backgroundFilePath, resume);
        activity.submitRequest(bgChangeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(bgChangeRequest, true);
                callback.invoke(callback, bgChangeRequest, e);
            }
        });
    }
}
