package com.onyx.android.sdk.scribble.asyncrequest.shape;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.navigation.PageFlushRequest;
import com.onyx.android.sdk.scribble.data.TouchPoint;

/**
 * Created by john on 5/8/2017.
 */

public class ShapeSelectionRequest extends AsyncBaseNoteRequest {

    private static final String TAG = PageFlushRequest.class.getSimpleName();
    private volatile TouchPoint start;
    private volatile TouchPoint end;

    public ShapeSelectionRequest(final TouchPoint s, final TouchPoint e) {
        super();
        setAbortPendingTasks(false);
        setPauseInputProcessor(true);
        setResumeInputProcessor(false);
        start = new TouchPoint(s);
        end = new TouchPoint(e);
    }

    @Override
    public void execute(final AsyncNoteViewHelper helper) throws Exception {
        if (!helper.getNoteDocument().isOpen()) {
            return;
        }
        benchmarkStart();
        setRender(true);
        renderCurrentPageInBitmap(helper);
        renderSelectionRectangle(helper, start, end);
    }
}
