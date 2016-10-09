package com.onyx.kreader.ui.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 4/17/16.
 */
public class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

    private ReaderDataHolder readerDataHolder;

    public MyOnGestureListener(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return readerDataHolder.getHandlerManager().onSingleTapUp(readerDataHolder, e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        readerDataHolder.getHandlerManager().onLongPress(readerDataHolder, e);
    }

    // http://stackoverflow.com/questions/3081711/android-view-gesturedetector-ongesturelistener-onfling-vs-onscroll
    // keep on called
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return readerDataHolder.getHandlerManager().onScroll(readerDataHolder, e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return readerDataHolder.getHandlerManager().onFling(readerDataHolder, e1, e2, velocityX, velocityY);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return readerDataHolder.getHandlerManager().onDown(readerDataHolder, e);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return readerDataHolder.getHandlerManager().onSingleTapConfirmed(readerDataHolder, e);
    }
}
