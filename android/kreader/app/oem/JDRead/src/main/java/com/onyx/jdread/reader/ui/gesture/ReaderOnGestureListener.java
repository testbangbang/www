package com.onyx.jdread.reader.ui.gesture;

import android.view.GestureDetector;
import android.view.MotionEvent;

import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 4/17/16.
 */
public class ReaderOnGestureListener extends GestureDetector.SimpleOnGestureListener {
    private static final String TAG = ReaderOnGestureListener.class.getSimpleName();

    private ReaderDataHolder readerDataHolder;

    public ReaderOnGestureListener(final ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        return readerDataHolder.getHandlerManger().onSingleTapUp(event);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return readerDataHolder.getHandlerManger().onSingleTapConfirmed(event);
    }

    @Override
    public void onLongPress(MotionEvent event) {
        readerDataHolder.getHandlerManger().onLongPress(event);
    }

    // http://stackoverflow.com/questions/3081711/android-view-gesturedetector-ongesturelistener-onfling-vs-onscroll
    // keep on called
    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        return readerDataHolder.getHandlerManger().onScroll(event1, event2, distanceX, distanceY);
    }

    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
        return super.onFling(event1, event2, velocityX, velocityY);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        return readerDataHolder.getHandlerManger().onDown(event);
    }
}
