package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;

/**
 * Created by zhuzeng on 6/27/16.
 */
public class DocumentEditAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private volatile String uniqueId;
    private volatile String parentUniqueId;

    public DocumentEditAction(final String id, final String parentId) {
        uniqueId = id;
        parentUniqueId = parentId;
    }

    public void execute(final T activity) {
        final NoteDocumentOpenRequest openRequest = new NoteDocumentOpenRequest(uniqueId, parentUniqueId);
        activity.getNoteViewHelper().submit(activity, openRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.onRequestFinished(true);
            }
        });
    }
}

