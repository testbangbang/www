package com.onyx.edu.reader.note.request;

import com.onyx.edu.reader.note.NoteManager;

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
