package com.onyx.edu.reader.ui.gesture;

import android.view.ScaleGestureDetector;

import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 4/17/16.
 */
public class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = MyScaleGestureListener.class.getSimpleName();
    private static int MIN_SCALE_TIME_MS = 200;

    private long lastScaleBeginTime = 0;
    private ReaderDataHolder readerDataHolder;

    public MyScaleGestureListener(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        readerDataHolder.getHandlerManager().onScaleEnd(readerDataHolder, detector);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (inMinScaleTime(lastScaleBeginTime)){
            lastScaleBeginTime = System.currentTimeMillis();
            return readerDataHolder.getHandlerManager().onScaleBegin(readerDataHolder, detector);
        }
        return false;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return readerDataHolder.getHandlerManager().onScale(readerDataHolder, detector);
    }

    private boolean inMinScaleTime(long lastTime){
        return System.currentTimeMillis() - lastTime > MIN_SCALE_TIME_MS;
    }
}