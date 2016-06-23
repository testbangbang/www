package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.ShapeViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentSaveRequest extends BaseNoteRequest {

    public NoteDocumentSaveRequest() {
    }

    public void execute(final ShapeViewHelper parent) throws Exception {
        parent.getNoteDocument().save();
    }

}
