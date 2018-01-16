package com.onyx.jdread.reader.handler;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.data.ControlType;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.android.sdk.data.KeyBinding;
import com.onyx.android.sdk.data.TouchAction;
import com.onyx.android.sdk.data.TouchBinding;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;

import java.util.Map;

/**
 * Created by huxiaomao on 17/5/8.
 */

public class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    public static final int TOUCH_HORIZONTAL_PART = 3;
    public static final int TOUCH_VERTICAL_PART = 2;
    private ReaderDataHolder readerDataHolder;
    private static Point startPoint = new Point();

    public BaseHandler(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    public ReaderDataHolder getReaderDataHolder() {
        return readerDataHolder;
    }

    public boolean isSingleTapUp() {
        return true;
    }

    public void setSingleTapUp(boolean singleTapUp) {

    }

    public void setActionUp(boolean actionUp) {

    }

    public boolean isActionUp() {
        return true;
    }


    public boolean onDown(MotionEvent event) {
        startPoint = new Point((int) event.getX(), (int) event.getY());
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        boolean ret = true;
        switch (keyCode) {
            case KeyEvent.KEYCODE_CLEAR:
                break;
            default:
                ret = false;
                break;
        }
        return ret;
    }

    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public boolean isLongPress() {
        return true;
    }

    public void setLongPress(boolean l) {

    }

    public Point getStartPoint() {
        return startPoint;
    }

    public boolean onSingleTapConfirmed(MotionEvent event) {
        return false;
    }

    public boolean onScrollAfterLongPress(float x1, float y1, float x2, float y2) {
        return false;
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public int panOffset() {
        return 150;
    }

    public void panLeft() {
        pan(-panOffset(), 0);
    }

    public void panRight() {
        pan(panOffset(), 0);
    }

    public void panUp() {
        pan(0, -panOffset());
    }

    public void panDown() {
        pan(0, panOffset());
    }

    public void pan(int x, int y) {
    }

    public boolean onSingleTapUp(MotionEvent event) {
        return tryHitTest(event.getX(), event.getY());
    }

    public boolean onScaleEnd(ScaleGestureDetector detector) {

        return true;
    }

    public boolean onScaleBegin(ScaleGestureDetector detector) {
        setPinchZooming(true);
        if (isSkipPinchZooming()) {
            //Toast.makeText(readerDataHolder.getContext(), R.string.pinch_zooming_can_not_be_used, Toast.LENGTH_SHORT).show();
            return true;
        }

        return true;
    }

    public boolean onScale(ScaleGestureDetector detector) {
        if (isSkipPinchZooming()) {
            return true;
        }
        return true;
    }

    public boolean onActionUp(MotionEvent event) {
        if (isLongPress()) {
        } else if (isScrolling() && !isPinchZooming()) {
            panFinished((int) (getStartPoint().x - event.getX()), (int) (getStartPoint().y - event.getY()));
        }
        resetState();
        return true;
    }

    public boolean onActionCancel(MotionEvent event) {
        return true;
    }

    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        if (isPinchZooming()) {
            // scrolling may happens after pinch zoom, so we always reset scroll state to avoid conflicts
            setScrolling(false);
            return true;
        }
        setScrolling(true);
        getStartPoint().set((int) event1.getX(), (int) event1.getY());
        if (event2.getAction() == MotionEvent.ACTION_MOVE) {
            panning((int) (event2.getX() - getStartPoint().x), (int) (event2.getY() - getStartPoint().y));
        }
        return true;
    }

    public void onLongPress(MotionEvent event) {
        setLongPress(true);
    }

    public void panning(int offsetX, int offsetY) {
    }

    public void panFinished(int offsetX, int offsetY) {
    }

    private Point bookmarkPosition(Bitmap bitmap) {
        Point point = new Point();
        point.set(readerDataHolder.getReader().getReaderViewHelper().getPageViewWidth() - bitmap.getWidth(), 10);
        return point;
    }

    public void showReaderMenu() {
    }

    public boolean isScrolling() {
        return true;
    }

    public void setScrolling(boolean s) {

    }

    public void setPinchZooming(boolean s) {

    }

    public boolean isPinchZooming() {
        return true;
    }

    public boolean isSkipPinchZooming() {
        return true;
    }

    public void resetState() {

    }

    public void close() {
    }

    public void resetTouchStartPosition() {
        startPoint = null;
    }

    public void setTouchStartEvent(MotionEvent event) {
        if (startPoint == null) {
            startPoint = new Point((int) event.getX(), (int) event.getY());
        }
    }

    public static String getTouchAreaCode(ReaderDataHolder readerDataHolder, final MotionEvent event) {
        int displayWidth = readerDataHolder.getReader().getReaderViewHelper().getPageViewWidth();
        int displayHeight = readerDataHolder.getReader().getReaderViewHelper().getPageViewHeight();
        if (event.getX() > displayWidth * TOUCH_VERTICAL_PART / TOUCH_HORIZONTAL_PART &&
                event.getY() > displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_RIGHT_BOTTOM;
        } else if (event.getX() < displayWidth / TOUCH_HORIZONTAL_PART &&
                event.getY() > displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_LEFT_BOTTOM;
        } else if (event.getX() < displayWidth / TOUCH_HORIZONTAL_PART &&
                event.getY() < displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_LEFT_TOP;
        } else if (event.getX() > displayWidth * TOUCH_VERTICAL_PART / TOUCH_HORIZONTAL_PART &&
                event.getY() < displayHeight / TOUCH_VERTICAL_PART) {
            return TouchBinding.TOUCH_RIGHT_TOP;
        } else {
            return TouchBinding.TOUCH_CENTER;
        }
    }

    public static boolean processSingleTapUp(ReaderDataHolder readerDataHolder, final String action, final String args) {
        return true;
    }

    public static CustomBindKeyBean getControlBinding(final ControlType controlType, final String controlCode) {
        Map<String, CustomBindKeyBean> map = controlType == ControlType.KEY ? KeyBinding.defaultValue().getHandlerManager() : TouchBinding.defaultValue().getBindingMap();
        if (map == null) {
            return null;
        }
        return map.get(controlCode);
    }

    public void onStop() {

    }

    public boolean tryHitTest(float x, float y) {

        return false;
    }
}
