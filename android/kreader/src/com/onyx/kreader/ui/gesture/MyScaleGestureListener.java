package com.onyx.kreader.ui.gesture;

import android.view.ScaleGestureDetector;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 4/17/16.
 */
public class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
    private ReaderActivity readerActivity;

    public MyScaleGestureListener(final ReaderActivity activity) {
        readerActivity = activity;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        readerActivity.getHandlerManager().onScaleEnd(readerActivity, detector);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return readerActivity.getHandlerManager().onScaleBegin(readerActivity, detector);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return readerActivity.getHandlerManager().onScale(readerActivity, detector);
    }
}