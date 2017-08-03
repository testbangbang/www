package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.util.Log;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 29/7/2017.
 */

public class RenderInBackgroundRequest extends AsyncBaseNoteRequest {

    private static final String TAG = PageFlushRequest.class.getSimpleName();

    private List<Shape> shapeList = new ArrayList<>();


    public RenderInBackgroundRequest(final List<Shape> list) {
        super();
        setAbortPendingTasks(false);
        shapeList.addAll(list);
        setPauseInputProcessor(false);
        setResumeInputProcessor(false);
    }

    @Override
    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        if (!helper.getNoteDocument().isOpen()) {
            return;
        }
        benchmarkStart();
        helper.getNoteDocument().getCurrentPage(getContext()).addShapeList(shapeList);
        setRender(true);
        renderCurrentPage(helper);
        setRender(false);
        Log.e(TAG, "render takes: " + benchmarkEnd());
    }

}
