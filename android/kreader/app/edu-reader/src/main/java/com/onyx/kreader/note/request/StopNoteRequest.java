package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.kreader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 10/6/16.
 */

public class StopNoteRequest extends ReaderBaseNoteRequest {

    private volatile boolean stop = false;

    public StopNoteRequest(boolean s) {
        stop = s;
        setAbortPendingTasks(false);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        noteManager.enableScreenPost(true);
        noteManager.enableRawEventProcessor(false);
        noteManager.resetSelection();
        if (stop) {
            noteManager.stopRawEventProcessor();
        }
    }
}
