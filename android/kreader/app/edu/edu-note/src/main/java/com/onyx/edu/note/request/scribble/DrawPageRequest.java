package com.onyx.edu.note.request.scribble;

import android.graphics.Bitmap;
import android.view.SurfaceView;

import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.edu.note.util.NoteViewUtil;

import java.util.List;

/**
 * Created by solskjaer49 on 2017/7/6 11:03.
 */

public class DrawPageRequest extends BaseNoteRequest {
    private volatile SurfaceView mSurfaceView;
    private volatile Bitmap mViewBitmap;
    private volatile List<Shape> mStashList;

    public DrawPageRequest(SurfaceView surfaceView, Bitmap viewBitmap, List<Shape> stashList) {
        mSurfaceView = surfaceView;
        mViewBitmap = viewBitmap;
        mStashList = stashList;
    }

    @Override
    public void execute(NoteViewHelper helper) throws Exception {
        benchmarkStart();
        NoteViewUtil.drawPage(mSurfaceView, mViewBitmap, mStashList);
        benchmarkEnd();
    }
}
