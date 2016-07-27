package com.onyx.kreader.ui.handler;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.actions.SelectWordAction;
import com.onyx.kreader.ui.data.ReaderConfig;
import com.onyx.kreader.ui.highlight.HighlightCursor;
import com.onyx.kreader.utils.MathUtils;
import com.onyx.kreader.utils.RectUtils;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/23/14
 * Time: 11:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class WordSelectionHandler extends BaseHandler {

    private static final String TAG = "WordSelectionHandler";
    private final static long TOUCH_DOWN_TO_MOVE_THRESHOLD = 200; // ms

    private int selectionMoveDistanceThreshold;

    private Point lastMovedPoint = null;
    private int cursorSelected = -1;
    private boolean moveAfterLongPress = false;

    private long touchDownTime = -1;
    private long touchMoveTime = -1;
    private long lastTouchMoveTime = -1;

    public WordSelectionHandler(HandlerManager parent, final Context context) {
        super(parent);
        selectionMoveDistanceThreshold = ReaderConfig.sharedInstance(context).getSelectionMoveDistanceThreshold();
    }

    public void onLongPress(ReaderActivity activity, final float x1, final float y1, final float x2, final float y2) {
        if (!activity.hasSelectionWord()) {
             super.onLongPress(activity, x1, y1, x2, y2);
        } else {
            activity.highlight(x1, y1, x2, y2);
        }
    }

    @Override
    public boolean onDown(ReaderActivity activity, MotionEvent e) {
        touchDownTime = System.currentTimeMillis();
        cursorSelected = activity.getCursorSelected((int)e.getX(), (int)e.getY());
        return super.onDown(activity, e);
    }

    public boolean onScroll(ReaderActivity activity,  MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    public boolean onSingleTapUp(ReaderActivity activity,  MotionEvent e) {
        activity.selectWord(getParent().getTouchStartPosition().x, getParent().getTouchStartPosition().y, e.getX(), e.getY(), false);
        return true;
    }

    public boolean onActionUp(ReaderActivity activity, final float startX, final float startY, final float endX, final float endY) {
        activity.highlightFinished(startX, startY, endX, endY,moveAfterLongPress);
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
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
            case KEYCDOE_ERASE_KK:
            case KEYCDOE_SCRIBE_KK:
            case KEYCDOE_SCRIBE:
            case KEYCDOE_ERASE:
                return false;
            case KeyEvent.KEYCODE_BACK:
                activity.quitWordSelection();
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_PAGE_UP:
                activity.quitWordSelection();
                return super.onKeyDown(activity,keyCode,event);
            default:
                return super.onKeyDown(activity,keyCode,event);
        }
    }

    /**
     * Check if the current touch point is too close with last processed one
     * @param x
     * @param y
     * @return
     */
    private boolean checkIfPointTooClose(int x, int y) {
        if (lastMovedPoint == null) {
            PointF startPoint = getParent().getTouchStartPosition();
            lastMovedPoint = new Point((int)startPoint.x, (int)startPoint.y);
        }

        if (MathUtils.distance(lastMovedPoint.x, lastMovedPoint.y, x, y) < selectionMoveDistanceThreshold) {
            return true;
        }
        lastMovedPoint.set(x, y);
        return false;
    }

    @Override
    public boolean onTouchEvent(ReaderActivity activity, MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveAfterLongPress = false;
                break;
            case MotionEvent.ACTION_MOVE:
                touchMoveTime = System.currentTimeMillis();
                boolean cursorNotSelected = cursorSelected < 0;
                if (cursorNotSelected && !this.moveAfterLongPress) {
                    return true;
                }
                if (touchMoveTime - touchDownTime < TOUCH_DOWN_TO_MOVE_THRESHOLD) {
                    return true;
                }

                if (checkIfPointTooClose((int)x, (int)y)) {
                    return true;
                }

                // the move point submit frequency is too high, ignore this move point
                if (touchMoveTime - lastTouchMoveTime < 80) {
                    return true;
                }
                lastTouchMoveTime = touchMoveTime;
                moveAfterLongPress = true;
                highlightAlongTouchMoved(activity,x, y, cursorSelected);
                return true;
            default:
                break;
        }
        return super.onTouchEvent(activity, e);
    }

    public void highlightAlongTouchMoved(ReaderActivity activity,float x, float y, int cursorSelected) {
        ReaderSelection selection = activity.getReaderUserDataInfo().getHighlightResult();
        PageInfo pageInfo = activity.getReaderViewInfo().getPageInfo(selection.getPagePosition());
        if (hitTestPage(activity,x, y) != pageInfo) {
            return;
        }
        if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
            PointF leftTop = new PointF(x, y);
            PointF bottomRight = RectUtils.getBottomRight(selection.getRectangles());
            if (isTopToBottom(leftTop,bottomRight)){
                new SelectWordAction(pageInfo.getName(), leftTop, bottomRight, true).execute(activity);
            }
        } else {
            PointF leftTop = RectUtils.getTopLeft(selection.getRectangles());
            PointF bottomRight = new PointF(x, y);
            if (isTopToBottom(leftTop,bottomRight)){
                new SelectWordAction(pageInfo.getName(), leftTop, bottomRight, true).execute(activity);
            }
        }
    }

    private PageInfo hitTestPage(ReaderActivity activity,float x, float y) {
        if (activity.getReaderViewInfo().getVisiblePages() == null) {
            return null;
        }
        for (PageInfo pageInfo : activity.getReaderViewInfo().getVisiblePages()) {
            if (pageInfo.getDisplayRect().contains(x, y)) {
                return pageInfo;
            }
        }
        return null;
    }

    private boolean isTopToBottom(PointF leftTop,PointF bottomRight){
        return bottomRight.y >= leftTop.y;
    }
}
