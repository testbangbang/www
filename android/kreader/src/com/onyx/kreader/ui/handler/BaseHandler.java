package com.onyx.kreader.ui.handler;


import android.graphics.Point;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.utils.StringUtils;


/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/19/14
 * Time: 8:42 PM
 * Basic event handler.
 */
public class BaseHandler  {

    public static  final int KEYCDOE_SCRIBE = 213;
    public static  final int KEYCDOE_ERASE = 214;
    public static  final int KEYCDOE_SCRIBE_KK = 226;
    public static  final int KEYCDOE_ERASE_KK = 227;


    private static final String TAG = BaseHandler.class.getSimpleName();

    private Point startPoint = new Point();

    private boolean scaling = false;
    private boolean scrolling = false;
    private boolean longPress = false;

    private HandlerManager parent;
    public BaseHandler(HandlerManager p) {
        super();
        parent = p;
    }

    public HandlerManager getParent() {
        return parent;
    }

    public boolean preKeyDown(ReaderActivity activity, int keyCode, KeyEvent event) {
        return false;
    }

    /**
     * Process key down event.
     * @param activity
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(ReaderActivity activity, int keyCode, KeyEvent event) {
        preKeyDown(activity, keyCode, event);

        final String key = KeyEvent.keyCodeToString(keyCode);
        final String action = getParent().getKeyAction(TAG, key);
        final String args = getParent().getKeyArgs(TAG, key);
        if (StringUtils.isNullOrEmpty(action)) {
            Log.w(TAG, "No action found for key: " + key);
        }

        return parent.processKeyDown(activity, action, args);
    }

    public boolean onKeyUp(ReaderActivity activity, int keyCode, KeyEvent event) {
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

    public boolean onTouchEvent(ReaderActivity activity, MotionEvent e) {
        return true;
    }

    public boolean onDown(ReaderActivity activity, MotionEvent e) {
        startPoint = new Point((int)e.getX(), (int)e.getY());
        return true;
    }

    public boolean onDoubleTap(ReaderActivity activity, MotionEvent e) {
        return false;
    }

    public void onShowPress(ReaderActivity activity, MotionEvent e) {

    }

    public boolean onSingleTapUp(ReaderActivity activity,  MotionEvent e) {
        if (activity.tryHitTest(e.getX(), e.getY())) {
            return true;
        } else if (e.getX() > activity.displayWidth() * 2 / 3) {
            activity.nextScreen();
        } else if (e.getX() < activity.displayWidth() / 3) {
            activity.prevScreen();
        } else {
            activity.showReaderMenu();
        }
        return true;
    }

    public boolean onSingleTapConfirmed(ReaderActivity activity, MotionEvent e) {
        return true;
    }

    public boolean onScaleEnd(ReaderActivity activity, ScaleGestureDetector detector) {
        activity.scaleEnd();
        setScaling(false);
        return true;
    }

    public boolean onScaleBegin(ReaderActivity activity, ScaleGestureDetector detector) {
        setScaling(true);
        activity.scaleBegin(detector);
        return true;
    }

    public boolean onScale(ReaderActivity activity, ScaleGestureDetector detector)  {
        activity.scaling(detector);
        return true;
    }

    protected Point getStartPoint() {
        return startPoint;
    }

    public boolean onActionUp(ReaderActivity activity, final float startX, final float startY, final float endX, final float endY) {
        if (isLongPress()) {
        } else if (isScrolling()) {
            activity.panFinished((int) (endX - getStartPoint().x), (int) (endY - getStartPoint().y));
        }
        resetState();
        return true;
    }

    public boolean onScroll(ReaderActivity activity,  MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isScaling()) {
            // scrolling may happens before scale, so we always reset scroll state to avoid conflicts
            setScrolling(false);
            return true;
        }
        setScrolling(true);
        getStartPoint().set((int)e1.getX(), (int)e1.getY());
        if (e2.getAction() == MotionEvent.ACTION_MOVE) {
            activity.panning((int)(e2.getX() - getStartPoint().x), (int)(e2.getY() - getStartPoint().y));
        }
        return true;
    }

    public boolean onScrollAfterLongPress(ReaderActivity activity, final float x1, final float y1, final float x2, final float y2) {
        activity.highlight(x1, y1, x2, y2);
        return true;
    }

    public void onLongPress(ReaderActivity activity, final float x1, final float y1, final float x2, final float y2) {
        setLongPress(true);
        activity.selectWord(x1, y1, x2, y2, true);
    }

    public boolean onFling(ReaderActivity activity, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public boolean isLongPress() {
        return longPress;
    }

    public void setLongPress(boolean l) {
        longPress = l;
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(boolean s) {
        scrolling = s;
    }

    public void setScaling(boolean s) {
        scaling = s;
    }

    public boolean isScaling() {
        return scaling;
    }

    public void resetState() {
        scrolling = false;
        longPress = false;
    }

    public void setPenErasing(boolean c) {
        parent.setPenErasing(c);
    }

    public boolean isPenErasing() {
        return parent.isPenErasing();
    }

    public void setPenStart(boolean s) {
        parent.setPenStart(s);
    }

    public boolean isPenStart() {
        return parent.isPenStart();
    }

}
