package com.onyx.android.sdk.scribble.asyncrequest.note;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentSaveRequest extends AsyncBaseNoteRequest {

    private volatile String title;
    private volatile boolean close;

    public NoteDocumentSaveRequest(final String t, boolean c) {
        title = t;
        close = c;
        setPauseInputProcessor(true);
        setResumeInputProcessor(!close);
    }

    @Override
    public void execute(final AsyncNoteViewHelper parent) throws Exception {
        renderCurrentPage(parent);
        NoteDataProvider.saveThumbnailWithSize(getContext(),
                parent.getNoteDocument().getDocumentUniqueId(),
                parent.getRenderBitmap(),
                512,
                512);
        parent.save(getContext(), title, close);
    }

}
