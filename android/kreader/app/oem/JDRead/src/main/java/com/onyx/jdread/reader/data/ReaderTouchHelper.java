package com.onyx.jdread.reader.data;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.ui.gesture.ReaderOnGestureListener;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class ReaderTouchHelper {
    private ReaderDataHolder readerDataHolder;
    private GestureDetector gestureDetector;
    private SurfaceView surfaceView;

    public int getPageViewWidth() {
        return surfaceView.getWidth();
    }

    public int getPageViewHeight() {
        return surfaceView.getHeight();
    }

    public void setReaderDataHolder(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public void setReaderViewTouchListener(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        surfaceView.setOnTouchListener(onTouchEvent);
        gestureDetector = new GestureDetector(surfaceView.getContext(), new ReaderOnGestureListener(readerDataHolder));
    }

    public View.OnTouchListener onTouchEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            onTouchEvent(v, event);
            return true;
        }
    };

    private void onTouchEvent(View v, MotionEvent event) {
        if(readerDataHolder.isDocumentOpened()) {
            readerDataHolder.getHandlerManger().setTouchStartEvent(event);
            gestureDetector.onTouchEvent(event);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                readerDataHolder.getHandlerManger().onActionUp(event);
                readerDataHolder.getHandlerManger().resetTouchStartPosition();
            }
            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                readerDataHolder.getHandlerManger().onActionCancel(event);
                readerDataHolder.getHandlerManger().resetTouchStartPosition();
            }

            readerDataHolder.getHandlerManger().onTouchEvent(event);
        }
    }
}
