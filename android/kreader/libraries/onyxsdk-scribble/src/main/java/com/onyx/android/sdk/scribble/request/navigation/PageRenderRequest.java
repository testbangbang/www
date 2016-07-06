package com.onyx.android.sdk.scribble.request.navigation;

import android.util.Log;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class PageRenderRequest extends BaseNoteRequest {

    private static final String TAG = PageRenderRequest.class.getSimpleName();
    private volatile List<Shape> shapeList = new ArrayList<>();

    public PageRenderRequest(final List<Shape> list) {
        shapeList.addAll(list);
        setRender(true);
        setPauseInputProcessor(false);
        setResumeInputProcessor(false);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        long start = System.currentTimeMillis();
        helper.getNoteDocument().getCurrentPage(getContext()).addShapeList(shapeList);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
        long end = System.currentTimeMillis();
        Log.e(TAG, "Render in background finished: " + (end - start));
    }


}
