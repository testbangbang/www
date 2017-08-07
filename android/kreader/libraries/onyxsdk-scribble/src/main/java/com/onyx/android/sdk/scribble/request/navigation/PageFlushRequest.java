package com.onyx.android.sdk.scribble.request.navigation;

import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class PageFlushRequest extends BaseNoteRequest {
    private static final String TAG = PageFlushRequest.class.getSimpleName();

    private List<Shape> shapeList = new ArrayList<>();
    private volatile boolean save = false;

    public PageFlushRequest(final List<Shape> list, boolean r, boolean resume, final NoteDrawingArgs args) {
        super();
        setAbortPendingTasks(false);
        shapeList.addAll(list);
        setRender((shapeList.size() > 0) || r);
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
        syncDrawingArgs(args);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
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

    private void saveDocument(final NoteViewHelper helper) {
        if (!save) {
            return;
        }
        benchmarkStart();
        helper.getNoteDocument().save(getContext(), null);
        Log.e("Save all pages", " duration " + benchmarkEnd());
    }
}
