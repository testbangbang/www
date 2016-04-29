package com.onyx.kreader.ui.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;
import com.onyx.kreader.ui.ReaderActivity;

/**
 * Created by zhuzeng on 4/17/16.
 */
public class MyOnGestureListener extends GestureDetector.SimpleOnGestureListener {

    private ReaderActivity readerActivity;

    public MyOnGestureListener(final ReaderActivity activity) {
        readerActivity = activity;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return readerActivity.getHandlerManager().onSingleTapUp(readerActivity, e);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        readerActivity.getHandlerManager().onLongPress(readerActivity, e);
    }

    // http://stackoverflow.com/questions/3081711/android-view-gesturedetector-ongesturelistener-onfling-vs-onscroll
    // keep on called
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return readerActivity.getHandlerManager().onScroll(readerActivity, e1, e2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return readerActivity.getHandlerManager().onFling(readerActivity, e1, e2, velocityX, velocityY);
    }

    @Override
    public void onShowPress(MotionEvent e) {
        readerActivity.getHandlerManager().onShowPress(readerActivity, e);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return readerActivity.getHandlerManager().onDown(readerActivity, e);
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        return readerActivity.getHandlerManager().onDoubleTap(readerActivity, e);
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return readerActivity.getHandlerManager().onSingleTapConfirmed(readerActivity, e);
    }
}
