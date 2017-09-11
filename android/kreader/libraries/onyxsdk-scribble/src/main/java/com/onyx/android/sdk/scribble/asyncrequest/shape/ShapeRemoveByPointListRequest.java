package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.util.Log;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.TouchPointList;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class ShapeRemoveByPointListRequest extends AsyncBaseNoteRequest {

    private volatile TouchPointList touchPointList;
    public ShapeRemoveByPointListRequest(final TouchPointList list) {
        touchPointList = list;
        setPauseInputProcessor(true);
    }

    @Override
    public void execute(final NoteManager noteManager) throws Exception {
        setResumeInputProcessor(noteManager.useDFBForCurrentState());
        benchmarkStart();
        noteManager.getNoteDocument().removeShapesByTouchPointList(getContext(), touchPointList, 1.0f);
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
        Log.e("############", "erase takes: " + benchmarkEnd());
    }

}
