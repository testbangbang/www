package com.onyx.kreader.ui.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.bridge.NoteEventProcessorManager;
import com.onyx.kreader.ui.data.ReaderDataHolder;


/**
 * Created by Joy on 2014/3/26.
 */
public class ScribbleHandler extends BaseHandler {

    public ScribbleHandler(HandlerManager p) {
        super(p);
    }

    public boolean preKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        return true;
    }

    private boolean isEnableBigPen() {
        return true;
    }

    private NoteEventProcessorManager getNoteEventProcessorManager() {
        return getParent().getReaderDataHolder().getNoteManager().getNoteEventProcessorManager();
    }

    @Override
    public boolean onKeyDown(ReaderDataHolder readerDataHolder, int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return false;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return false;
            case KeyEvent.KEYCODE_DPAD_UP:
                return false;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return false;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return false;
            case KeyEvent.KEYCODE_MENU:
                return false;
            case KEYCDOE_ERASE:
            case KEYCDOE_ERASE_KK:
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
                if (isEnableBigPen()) {
                    getParent().setActiveProvider(HandlerManager.ERASER_PROVIDER);
                }
                return false;
            case KEYCDOE_SCRIBE:
            case KEYCDOE_SCRIBE_KK:
                return false;
            default:
                return super.onKeyDown(readerDataHolder,keyCode,event);
        }
    }

    @Override
    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (e.getPointerCount() > 1) {
            return false;
        }

        return getNoteEventProcessorManager().onTouchEvent(e);
    }

    @Override
    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(ReaderDataHolder readerDataHolder, MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScaleEnd(ReaderDataHolder ReaderDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY)  {
        return false;
    }

    @Override
    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onScrollAfterLongPress(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        return false;
    }

    @Override
    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {

    }

    @Override
    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

}
