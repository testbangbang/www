package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import android.util.Log;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class PageFlushRequest extends AsyncBaseNoteRequest {
    private static final String TAG = PageFlushRequest.class.getSimpleName();

    private List<Shape> shapeList = new ArrayList<>();
    private volatile boolean save = false;

    public PageFlushRequest(final List<Shape> list, boolean render, boolean resume, final NoteDrawingArgs args) {
        super();
        setAbortPendingTasks(false);
        shapeList.addAll(list);
        setRender((shapeList.size() > 0) || render);
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
        syncDrawingArgs(args);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        if (!noteManager.getNoteDocument().isOpen()) {
            return;
        }
        noteManager.getNoteDocument().getCurrentPage(getContext()).addShapeList(shapeList);
        noteManager.updateDrawingArgs(getDrawingArgs());
        renderCurrentPageInBitmap(noteManager);
        saveDocument(noteManager);
        updateShapeDataInfo(noteManager);
        setResumeInputProcessor(noteManager.useDFBForCurrentState() && isResumeInputProcessor());
    }

    private void saveDocument(final NoteManager noteManager) {
        if (!save) {
            return;
        }
        benchmarkStart();
        noteManager.getNoteDocument().save(getContext(), null);
        Log.e("Save all pages", " duration " + benchmarkEnd());
    }
}
