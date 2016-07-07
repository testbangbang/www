package com.onyx.android.sdk.scribble.request.shape;

import android.graphics.Matrix;
import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class ShapeRemoveByPointListRequest extends BaseNoteRequest {

    private volatile TouchPointList touchPointList;
    public ShapeRemoveByPointListRequest(final TouchPointList list) {
        touchPointList = list;
        setPauseInputProcessor(true);
        setResumeInputProcessor(true);
    }

    // remove and render.
    public void execute(final NoteViewHelper helper) throws Exception {
        Log.e("############", "erasing with points list size " + touchPointList.size());
        benchmarkStart();
        helper.getNoteDocument().removeShapesByTouchPointList(getContext(), touchPointList, 1.0f);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
        Log.e("############", "erase takes: " + benchmarkEnd());
    }

}
