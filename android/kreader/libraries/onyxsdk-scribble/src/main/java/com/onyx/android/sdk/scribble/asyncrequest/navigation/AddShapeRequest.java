package com.onyx.android.sdk.scribble.asyncrequest.navigation;

import android.util.Log;

import com.onyx.android.sdk.scribble.asyncrequest.AsyncBaseNoteRequest;
import com.onyx.android.sdk.scribble.asyncrequest.NoteManager;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class AddShapeRequest extends AsyncBaseNoteRequest {

    private static final String TAG = AddShapeRequest.class.getSimpleName();
    private volatile List<Shape> shapeList = Collections.synchronizedList(new ArrayList<Shape>());

    public AddShapeRequest(final List<Shape> list) {
        for(Shape shape : list) {
            Log.e("######", "origin shape with size: " + shape.getPoints().size());
        }
        shapeList.addAll(list);
        setPauseInputProcessor(false);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteManager parent) throws Exception {
        long start = System.currentTimeMillis();
        parent.getNoteDocument().getCurrentPage(getContext()).addShapeList(shapeList);
        renderCurrentPageInBitmap(parent);
        updateShapeDataInfo(parent);
        long end = System.currentTimeMillis();
        Log.e(TAG, "Render in background finished: " + (end - start));
    }


}
