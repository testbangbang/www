package com.onyx.android.note.actions.scribble;

import com.onyx.android.note.actions.BaseNoteAction;
import com.onyx.android.note.activity.BaseScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentRemoveRequest;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentDiscardAction<T extends BaseScribbleActivity> extends BaseNoteAction<T> {

    private volatile String uniqueId;

    public DocumentDiscardAction(final String id) {
        uniqueId = id;
    }

    public void execute(final T activity) {
        execute(activity, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.finish();
            }
        });
    }

    @Override
    public void execute(final T activity, final BaseCallback callback) {
        final NoteDocumentRemoveRequest removeRequest = new NoteDocumentRemoveRequest(uniqueId);
        activity.submitRequest(removeRequest, callback);
    }
}
