package com.onyx.kreader.ui.gesture;

import android.view.ScaleGestureDetector;

import com.onyx.kreader.common.Debug;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 4/17/16.
 */
public class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = MyScaleGestureListener.class.getSimpleName();
    private static int MIN_SCALE_TIME_MS = 200;

    private long lastScaleBeginTime = 0;
    private long lastScaleEndTime = 0;
    private long lastScalingTime = 0;
    private ReaderDataHolder readerDataHolder;

    public MyScaleGestureListener(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        Debug.d(TAG,"onScaleEnd");
        if (inMinScaleTime(lastScaleEndTime)){
            lastScaleEndTime = System.currentTimeMillis();
            readerDataHolder.getHandlerManager().onScaleEnd(readerDataHolder, detector);
        }
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Debug.d(TAG,"onScaleBegin");
        if (inMinScaleTime(lastScaleBeginTime)){
            lastScaleBeginTime = System.currentTimeMillis();
            return readerDataHolder.getHandlerManager().onScaleBegin(readerDataHolder, detector);
        }
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Debug.d(TAG,"onScaling");
//        if (inMinScaleTime(lastScalingTime)){
//            lastScalingTime = System.currentTimeMillis();
//            return readerDataHolder.getHandlerManager().onScale(readerDataHolder, detector);
//        }
        return readerDataHolder.getHandlerManager().onScale(readerDataHolder, detector);
    }

    private boolean inMinScaleTime(long lastTime){
        return System.currentTimeMillis() - lastTime > MIN_SCALE_TIME_MS;
    }
}