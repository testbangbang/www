package com.onyx.edu.reader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.edu.reader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 10/6/16.
 */

public class StartNoteRequest extends ReaderBaseNoteRequest {

    public StartNoteRequest(final List<PageInfo> list) {
        setAbortPendingTasks(false);
        setVisiblePages(list);
        setResetNoteDataInfo(false);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        setVisiblePages(getVisiblePages());
        noteManager.startRawEventProcessor();
        noteManager.enableRawEventProcessor(true);
        noteManager.setCurrentShapeType(noteManager.getNoteDocument().getCurrentShapeType());
        noteManager.setCurrentShapeColor(NoteDrawingArgs.defaultColor());
        noteManager.setNoteDirty(true);
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
    }

}
