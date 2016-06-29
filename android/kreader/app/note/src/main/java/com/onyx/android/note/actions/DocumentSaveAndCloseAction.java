package com.onyx.android.note.actions;

import com.onyx.android.note.activity.ScribbleActivity;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentOpenRequest;
import com.onyx.android.sdk.scribble.request.note.NoteDocumentSaveRequest;

/**
 * Created by zhuzeng on 6/29/16.
 */
public class DocumentSaveAndCloseAction<T extends ScribbleActivity> extends BaseNoteAction<T> {

    private String title;

    public DocumentSaveAndCloseAction(final String t) {
        title = t;
    }

    public void execute(final T activity) {
        final NoteDocumentSaveRequest saveRequest = new NoteDocumentSaveRequest(title);
        activity.getNoteViewHelper().submit(activity, saveRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                activity.finish();
            }
        });
    }
}
