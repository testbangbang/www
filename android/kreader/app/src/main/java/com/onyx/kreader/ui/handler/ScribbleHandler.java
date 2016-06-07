package com.onyx.kreader.ui.handler;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.onyx.kreader.scribble.data.ShapePage;
import com.onyx.kreader.scribble.shape.Shape;
import com.onyx.kreader.ui.ReaderActivity;


/**
 * Created by Joy on 2014/3/26.
 */
public class ScribbleHandler extends BaseHandler {

    private ShapePage shapePage = new ShapePage();

    public ScribbleHandler(HandlerManager p) {
        super(p);
    }

    public boolean preKeyDown(ReaderActivity activity, int keyCode, KeyEvent event) {
        return true;
    }

    private boolean isEnableBigPen() {
        return true;
    }

    @Override
    public boolean onKeyDown(ReaderActivity activity, int keyCode, KeyEvent event) {
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
                return super.onKeyDown(activity,keyCode,event);
        }
    }

    @Override
    public boolean onTouchEvent(ReaderActivity activity, MotionEvent e) {
        if (e.getPointerCount() > 1) {
            return false;
        }

        final Shape shape = shapePage.getShapeFromPool();
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case (MotionEvent.ACTION_DOWN):
                shape.onDown(null, null);
                return true;
            case (MotionEvent.ACTION_CANCEL):
            case (MotionEvent.ACTION_OUTSIDE):
                break;
            case MotionEvent.ACTION_UP:
                shape.onUp(null, null);
                return true;
            case MotionEvent.ACTION_MOVE:
                int n = e.getHistorySize();
                for (int i = 0; i < n; i++) {
                    shape.onMove(null, null);
                }
                shape.onMove(null, null);
                return true;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onDown(ReaderActivity activity, MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(ReaderActivity activity, MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(ReaderActivity activity, MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(ReaderActivity activity, MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(ReaderActivity activity, MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScaleEnd(ReaderActivity activity, ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ReaderActivity activity, ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScale(ReaderActivity activity, ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onActionUp(ReaderActivity activity, final float startX, final float startY, final float endX, final float endY)  {
        return false;
    }

    @Override
    public boolean onScroll(ReaderActivity activity, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onScrollAfterLongPress(ReaderActivity activity, float x1, float y1, float x2, float y2) {
        return false;
    }

    @Override
    public void onLongPress(ReaderActivity activity, final float x1, final float y1, final float x2, final float y2) {

    }

    @Override
    public boolean onFling(ReaderActivity activity, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
