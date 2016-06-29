package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentSaveRequest extends BaseNoteRequest {

    private volatile String title;

    public NoteDocumentSaveRequest(final String t) {
        title = t;
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        parent.getNoteDocument().save(getContext(), title);
    }

}
