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
    private int sideNoteStartSubPageIndex = 0;

    public StartNoteRequest(final List<PageInfo> list) {
        setAbortPendingTasks(false);
        setVisiblePages(list);
        setResetNoteDataInfo(false);
    }

    public StartNoteRequest(final List<PageInfo> list, boolean sideNoting, int sideNoteStartSubPageIndex) {
        setAbortPendingTasks(false);
        setVisiblePages(list);
        setResetNoteDataInfo(false);

        this.sideNoting = sideNoting;
        this.sideNoteStartSubPageIndex = sideNoteStartSubPageIndex;
    }

    public void execute(final NoteManager noteManager) throws Exception {
        ensureDocumentOpened(noteManager);
        noteManager.setSideNoting(sideNoting);
        noteManager.setSideNoteStartSubPageIndex(sideNoteStartSubPageIndex);
        noteManager.setVisiblePages(getVisiblePages());
        noteManager.startRawEventProcessor();
        noteManager.resumeRawEventProcessor(getContext());
        noteManager.enableRawEventProcessor(true);
        // we don't want current shape to be eraser when entering note writing mode,
        // which is confusing
        noteManager.setCurrentShapeType(noteManager.isEraser() ?
                ShapeFactory.SHAPE_PENCIL_SCRIBBLE :
                noteManager.getNoteDocument().getCurrentShapeType());
        noteManager.setCurrentShapeColor(NoteDrawingArgs.defaultColor());
        noteManager.restoreStrokeWidth();
        noteManager.setNoteDirty(true);
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

}
