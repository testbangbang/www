package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.util.Log;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncNoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class SelectShapeByPointListRequest extends AsyncBaseNoteRequest {

    private volatile TouchPointList touchPointList;
    private List<Shape> selectResultList = new ArrayList<>();

    public SelectShapeByPointListRequest(final TouchPointList list) {
        touchPointList = list;
        setPauseInputProcessor(true);
    }

    @Override
    public void execute(final NoteManager noteManager) throws Exception {
        setResumeInputProcessor(noteManager.useDFBForCurrentState());
        benchmarkStart();
        selectResultList = noteManager.getNoteDocument().selectShapesByTouchPointList(getContext(), touchPointList, 1.0f);
        renderCurrentPageInBitmap(noteManager);
        updateShapeDataInfo(noteManager);
        Log.e("############", "shape select takes: " + benchmarkEnd());
    }

    public List<Shape> getSelectResultList() {
        return selectResultList;
    }
}
