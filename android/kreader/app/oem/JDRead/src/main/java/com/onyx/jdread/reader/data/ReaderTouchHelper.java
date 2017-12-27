package com.onyx.jdread.reader.data;

import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class ReaderTouchHelper {
    private int readerMode = 0;

    public void setReaderMode(int readerMode) {
        this.readerMode = readerMode;
    }

    public void setReaderViewTouchListener(SurfaceView surfaceView) {
        surfaceView.setOnTouchListener(onTouchEvent);
    }

    public View.OnTouchListener onTouchEvent = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            onTouchEvent(v, event);
            return true;
        }
    };

    private void onTouchEvent(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (readerMode == 0) {
                RegionFunctionManager.processRegionFunction((int) event.getX(), (int) event.getY());
            }
        }
    }
}
