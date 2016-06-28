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

    public PageFlushRequest(final List<Shape> list) {
        shapeList = list;
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        helper.getNoteDocument().getCurrentPage(getContext()).addShapeList(shapeList);
        renderCurrentPage(helper);
        benchmarkStart();
        helper.getNoteDocument().save(getContext());
        Log.e("Save all pages", " duration " + benchmarkEnd());
        updateShapeDataInfo(helper);
    }
}
