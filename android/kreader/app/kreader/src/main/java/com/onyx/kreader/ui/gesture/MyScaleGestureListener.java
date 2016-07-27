package com.onyx.kreader.ui.gesture;

import android.view.ScaleGestureDetector;

import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 4/17/16.
 */
public class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = MyScaleGestureListener.class.getSimpleName();
    private static int MIN_SCALE_TIME_MS = 200;

    private long lastScaleBeginTime = 0;
    private long lastScaleEndTime = 0;
    private long lastScalingTime = 0;
    private ReaderActivity readerActivity;

    public MyScaleGestureListener(final ReaderActivity activity) {
        readerActivity = activity;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (inMinScaleTime(lastScaleEndTime)){
            lastScaleEndTime = System.currentTimeMillis();
            readerActivity.getHandlerManager().onScaleEnd(readerActivity, detector);
        }
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (inMinScaleTime(lastScaleBeginTime)){
            lastScaleBeginTime = System.currentTimeMillis();
            return readerActivity.getHandlerManager().onScaleBegin(readerActivity, detector);
        }
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (inMinScaleTime(lastScalingTime)){
            lastScalingTime = System.currentTimeMillis();
            return readerActivity.getHandlerManager().onScale(readerActivity, detector);
        }
        return false;
    }

    private boolean inMinScaleTime(long lastTime){
        return System.currentTimeMillis() - lastTime > MIN_SCALE_TIME_MS;
    }
}