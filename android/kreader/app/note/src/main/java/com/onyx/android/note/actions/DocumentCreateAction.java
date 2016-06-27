package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentCreateAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile String uniqueId;

    public DocumentCreateAction(final String id) {
        uniqueId = id;
    }

    public void execute(final T activity) {
        final NoteDocumentOpenRequest createRequest = new NoteDocumentOpenRequest(uniqueId);
        activity.getNoteViewHelper().submit(activity, createRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished();
            }
        });
    }
}
