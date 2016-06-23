package com.onyx.android.sdk.scribble;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.handler.BaseHandler;
import com.onyx.kreader.ui.handler.HandlerManager;


/**
 * Created by Joy on 2014/3/26.
 */
public class ScribbleHandler extends BaseHandler {

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
        final NotePage notePage = activity.getShapePage();
        if (notePage == null) {
            return false;
        }

        final PageInfo pageInfo = activity.getFirstPageInfo();
        final Shape shape = notePage.getShapeFromPool();
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case (MotionEvent.ACTION_DOWN):
                processDownEvent(shape, pageInfo, e);
                return true;
            case (MotionEvent.ACTION_CANCEL):
            case (MotionEvent.ACTION_OUTSIDE):
                break;
            case MotionEvent.ACTION_UP:
                processUpEvent(shape, pageInfo, e);
                addShape(notePage, shape);
                return true;
            case MotionEvent.ACTION_MOVE:
                processMoveEvent(shape, pageInfo, e);
                return true;
            default:
                break;
        }
        return true;
    }

    private void addShape(final NotePage notePage, final Shape shape) {
        notePage.setAddToActionHistory(true);
        notePage.addShape(shape);
    }

    private TouchPoint normalized(final PageInfo pageInfo, final MotionEvent e) {
        return ShapeUtils.normalize(pageInfo.getActualScale(), pageInfo.getDisplayRect().left, pageInfo.getDisplayRect().top, e);
    }

    private TouchPoint normalizedHistoricalPoint(final PageInfo pageInfo, final MotionEvent e, int index) {
        return ShapeUtils.normalize(pageInfo.getActualScale(), pageInfo.getDisplayRect().left, pageInfo.getDisplayRect().top,
                e.getHistoricalX(index),
                e.getHistoricalY(index),
                e.getHistoricalPressure(index),
                e.getHistoricalSize(index),
                e.getHistoricalEventTime(index));
    }

    public TouchPoint screenPoint(final PageInfo pageInfo, final MotionEvent e) {
        return new TouchPoint(e.getX() - pageInfo.getDisplayRect().left,
                e.getY() - pageInfo.getDisplayRect().top,
                e.getPressure(),
                e.getSize(),
                e.getEventTime());
    }

    public TouchPoint screenHistoricalPoint(final PageInfo pageInfo, final MotionEvent e, int index) {
        return new TouchPoint(e.getHistoricalX(index) - pageInfo.getDisplayRect().left,
                e.getHistoricalY(index) - pageInfo.getDisplayRect().top,
                e.getHistoricalPressure(index),
                e.getHistoricalSize(index),
                e.getHistoricalEventTime(index));
    }

    private void processDownEvent(final Shape shape, final PageInfo pageInfo, final MotionEvent e) {
        shape.onDown(normalized(pageInfo, e), screenPoint(pageInfo, e));
    }

    private void processUpEvent(final Shape shape, final PageInfo pageInfo, final MotionEvent e) {
        shape.onUp(normalized(pageInfo, e), screenPoint(pageInfo, e));
    }

    private void processMoveEvent(final Shape shape, final PageInfo pageInfo, final MotionEvent e) {
        int n = e.getHistorySize();
        for (int i = 0; i < n; i++) {
            shape.onMove(normalizedHistoricalPoint(pageInfo, e, i), screenHistoricalPoint(pageInfo, e, i));
        }
        shape.onMove(normalized(pageInfo, e), screenPoint(pageInfo, e));
    }

    private void renderShape(final Canvas canvas, final Paint paint, final Shape shape) {
        if (!shape.supportDFB()) {
            shape.render(null, canvas, paint);
        }
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
