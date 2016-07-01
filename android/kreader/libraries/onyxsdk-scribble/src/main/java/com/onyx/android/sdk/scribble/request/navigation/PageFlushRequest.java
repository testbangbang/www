package com.onyx.android.sdk.scribble.request.navigation;

import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 6/28/16.
 */
public class PageFlushRequest extends BaseNoteRequest {

    private volatile List<Shape> shapeList;
    private volatile boolean save = false;

    public PageFlushRequest(final List<Shape> list, boolean resume) {
        shapeList = list;
        setPauseInputProcessor(true);
        setResumeInputProcessor(resume);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        helper.getNoteDocument().getCurrentPage(getContext()).addShapeList(shapeList);
        renderCurrentPage(helper);
        saveDocument(helper);
        updateShapeDataInfo(helper);
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
