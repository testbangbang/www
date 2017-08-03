package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import android.util.Log;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
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

    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        if (!helper.getNoteDocument().isOpen()) {
            return;
        }
        helper.getNoteDocument().getCurrentPage(getContext()).addShapeList(shapeList);
        helper.updateDrawingArgs(getDrawingArgs());
        renderCurrentPage(helper);
        saveDocument(helper);
        updateShapeDataInfo(helper);
        setResumeInputProcessor(helper.useDFBForCurrentState() && isResumeInputProcessor());
    }

    private void saveDocument(final AsyncNoteViewHelper helper) {
        if (!save) {
            return;
        }
        benchmarkStart();
        helper.getNoteDocument().save(getContext(), null);
        Log.e("Save all pages", " duration " + benchmarkEnd());
    }
}
