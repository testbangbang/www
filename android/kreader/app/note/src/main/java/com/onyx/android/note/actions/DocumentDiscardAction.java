package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentRemoveRequest;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentDiscardAction <T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile String uniqueId;

    public DocumentDiscardAction(final String id) {
        uniqueId = id;
    }

    public void execute(final T activity, final BaseCallback callback) {
        final NoteDocumentRemoveRequest removeRequest = new NoteDocumentRemoveRequest(uniqueId);
        activity.getNoteViewHelper().submit(activity, removeRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.finish();
            }
        });
    }
}
