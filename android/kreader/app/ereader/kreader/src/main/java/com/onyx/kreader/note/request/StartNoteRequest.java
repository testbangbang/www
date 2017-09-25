package com.onyx.kreader.note.request;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Device;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NoteModel;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.kreader.note.NoteManager;

import java.util.List;

/**
 * Created by zhuzeng on 10/6/16.
 */

public class StartNoteRequest extends ReaderBaseNoteRequest {

    private boolean sideNoting = false;

    public StartNoteRequest(final List<PageInfo> list) {
        setAbortPendingTasks(false);
        setVisiblePages(list);
        setResetNoteDataInfo(false);
    }

    public StartNoteRequest(final List<PageInfo> list, boolean sideNoting) {
        setAbortPendingTasks(false);
        setVisiblePages(list);
        setResetNoteDataInfo(false);

        this.sideNoting = sideNoting;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        noteManager.setSideNoting(sideNoting);
        noteManager.setVisiblePages(getVisiblePages());
        noteManager.startRawEventProcessor();
        noteManager.resumeRawEventProcessor(getContext());
        noteManager.enableRawEventProcessor(true);
        noteManager.setCurrentShapeType(noteManager.getNoteDocument().getCurrentShapeType());
        noteManager.setCurrentShapeColor(NoteDrawingArgs.defaultColor());
        noteManager.setNoteDirty(true);
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
