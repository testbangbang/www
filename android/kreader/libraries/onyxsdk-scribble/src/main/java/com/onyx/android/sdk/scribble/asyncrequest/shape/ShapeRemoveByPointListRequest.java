package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.util.Log;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class ShapeRemoveByPointListRequest extends AsyncBaseNoteRequest {

    private volatile TouchPointList touchPointList;
    public ShapeRemoveByPointListRequest(final TouchPointList list) {
        touchPointList = list;
        setPauseInputProcessor(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        benchmarkStart();
        helper.getNoteDocument().removeShapesByTouchPointList(getContext(), touchPointList, 1.0f);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
        Log.e("############", "erase takes: " + benchmarkEnd());
    }

}
