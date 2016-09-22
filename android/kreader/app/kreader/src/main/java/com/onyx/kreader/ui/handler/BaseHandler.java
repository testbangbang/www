package com.onyx.kreader.ui.handler;

import android.graphics.Point;
import android.os.Debug;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.ui.actions.NextScreenAction;
import com.onyx.kreader.ui.actions.PanAction;
import com.onyx.kreader.ui.actions.PreviousScreenAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by ming on 16/7/27.
 */
public abstract class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();

    public static  final int KEYCDOE_SCRIBE = 213;
    public static  final int KEYCDOE_ERASE = 214;
    public static  final int KEYCDOE_SCRIBE_KK = 226;
    public static  final int KEYCDOE_ERASE_KK = 227;

    private Point startPoint = new Point();
    private HandlerManager parent;
    private boolean longPress;
    private boolean singleTapUp = false;
    private boolean actionUp = false;

    public boolean isSingleTapUp() {
        return singleTapUp;
    }

    public boolean isActionUp() {
        return actionUp;
    }

    public BaseHandler(HandlerManager parent){
        this.parent = parent;
    }

    public HandlerManager getParent() {
        return parent;
    }

    public void onActivate(final ReaderDataHolder readerDataHolder) {}

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {}

    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        startPoint = new Point((int)e.getX(), (int)e.getY());
        actionUp = false;
        singleTapUp = false;
        return true;
    }

    public boolean preKeyDown(ReaderDataHolder readerDataHolder,int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyDown(ReaderDataHolder readerDataHolder,int keyCode, KeyEvent event) {
        preKeyDown(readerDataHolder, keyCode, event);

        final String key = KeyEvent.keyCodeToString(keyCode);
        final String action = getParent().getKeyAction(TAG, key);
        final String args = getParent().getKeyArgs(TAG, key);
        if (StringUtils.isNullOrEmpty(action)) {
            Log.w(TAG, "No action found for key: " + key);
        }

        return parent.processKeyDown(readerDataHolder, action, args);
    }

    public boolean onKeyUp(ReaderDataHolder readerDataHolder,int keyCode, KeyEvent event) {
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

    public boolean onTouchEvent(ReaderDataHolder readerDataHolder,MotionEvent e) {
        return true;
    }

    public boolean isLongPress() {
        return longPress;
    }

    public void setLongPress(boolean l) {
        longPress = l;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public boolean onDoubleTap(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    public void onShowPress(ReaderDataHolder readerDataHolder, MotionEvent e) {
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        singleTapUp = true;
        return true;
    }

    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        return false;
    }

    public boolean onScale(ReaderDataHolder readerDataHolder,ScaleGestureDetector detector) {
        return false;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder,final float startX, final float startY, final float endX, final float endY)  {
        singleTapUp = false;
        actionUp = true;
        return true;
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public boolean onScrollAfterLongPress(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        return false;
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
        actionUp = false;
        setLongPress(true);
    }

    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void beforeProcessKeyDown(final ReaderDataHolder readerDataHolder) {}

    public void beforeChangePosition(final ReaderDataHolder readerDataHolder) {}

    public void afterChangePosition(final ReaderDataHolder readerDataHolder) {}

    public void nextPage(ReaderDataHolder readerDataHolder) {
        nextScreen(readerDataHolder);
    }

    public void prevPage(ReaderDataHolder readerDataHolder) {
        prevScreen(readerDataHolder);
    }

    public void nextScreen(ReaderDataHolder readerDataHolder) {
        readerDataHolder.setPreRenderNext(true);
        final NextScreenAction action = new NextScreenAction();
        action.execute(readerDataHolder);
    }

    public void prevScreen(ReaderDataHolder readerDataHolder) {
        readerDataHolder.setPreRenderNext(false);
        final PreviousScreenAction action = new PreviousScreenAction();
        action.execute(readerDataHolder);
    }

    public int panOffset() {
        return 150;
    }

    public void panLeft(final ReaderDataHolder readerDataHolder) {
        pan(readerDataHolder, -panOffset(), 0);
    }

    public void panRight(final ReaderDataHolder readerDataHolder) {
        pan(readerDataHolder, panOffset(), 0);
    }

    public void panUp(final ReaderDataHolder readerDataHolder) {
        pan(readerDataHolder, 0, -panOffset());
    }

    public void panDown(final ReaderDataHolder readerDataHolder) {
        pan(readerDataHolder, 0, panOffset());
    }

    public void pan(final ReaderDataHolder readerDataHolder, int x, int y) {
        final PanAction panAction = new PanAction(x, y);
        panAction.execute(readerDataHolder);
    }
}
