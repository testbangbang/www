package com.onyx.android.sdk.scribble.request.note;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDataProvider;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 6/23/16.
 */
public class NoteDocumentCloseRequest extends BaseNoteRequest {

    private volatile String title;

    public NoteDocumentCloseRequest(final String t) {
        title = t;
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper parent) throws Exception {
        renderCurrentPage(parent);
        NoteDataProvider.saveThumbnail(getContext(),
                parent.getNoteDocument().getDocumentUniqueId(),
                parent.getRenderBitmap());
        parent.close(getContext(), title);
    }

}
