package com.onyx.android.sdk.scribble.asyncrequest.shape;

import android.util.Log;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
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

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        benchmarkStart();
        selectResultList = helper.getNoteDocument().selectShapesByTouchPointList(getContext(), touchPointList, 1.0f);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
        Log.e("############", "shape select takes: " + benchmarkEnd());
    }

    public List<Shape> getSelectResultList() {
        return selectResultList;
    }
}
