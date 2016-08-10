package com.onyx.kreader.ui.handler;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.Debug;
import com.onyx.kreader.host.request.SelectWordRequest;
import com.onyx.kreader.ui.actions.SelectWordAction;
import com.onyx.kreader.ui.actions.ShowTextSelectionMenuAction;
import com.onyx.kreader.ui.data.ReaderConfig;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.PopupSelectionMenu;
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
public class WordSelectionHandler extends BaseHandler implements SelectWordAction.OnSelectWordCallBack {

    private static final String TAG = "WordSelectionHandler";
    private final static long TOUCH_DOWN_TO_MOVE_THRESHOLD = 200; // ms

    private int selectionMoveDistanceThreshold;

    private Point lastMovedPoint = null;
    private int cursorSelected = -1;
    private boolean moveAfterLongPress = false;

    private long touchDownTime = -1;
    private long touchMoveTime = -1;
    private long lastTouchMoveTime = -1;

    public WordSelectionHandler(HandlerManager parent, Context context) {
        super(parent);
        selectionMoveDistanceThreshold = ReaderConfig.sharedInstance(context).getSelectionMoveDistanceThreshold();
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
        if (!hasSelectionWord(readerDataHolder)) {
            super.onLongPress(readerDataHolder, x1, y1, x2, y2);
            selectWord(readerDataHolder,x1, y1, x2, y2, true);
        } else {
            highlight(readerDataHolder,x1, y1, x2, y2);
        }
    }

    @Override
    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        touchDownTime = System.currentTimeMillis();
        cursorSelected = getCursorSelected(readerDataHolder,(int)e.getX(), (int)e.getY());
        return super.onDown(readerDataHolder, e);
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return true;
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
//        selectWord(readerDataHolder,getParent().getTouchStartPosition().x, getParent().getTouchStartPosition().y, e.getX(), e.getY(), false);
        return true;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
//        highlightFinished(readerDataHolder,startX, startY, endX, endY,moveAfterLongPress);
        return true;
    }

    public boolean onScrollAfterLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
        highlight(readerDataHolder, x1, y1, x2, y2);
        return true;
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
            case KeyEvent.KEYCODE_ALT_LEFT:
            case KeyEvent.KEYCODE_ALT_RIGHT:
            case KEYCDOE_ERASE_KK:
            case KEYCDOE_SCRIBE_KK:
            case KEYCDOE_SCRIBE:
            case KEYCDOE_ERASE:
                return false;
            case KeyEvent.KEYCODE_BACK:
                quitWordSelection(readerDataHolder);
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_PAGE_UP:
                quitWordSelection(readerDataHolder);
                return super.onKeyDown(readerDataHolder,keyCode,event);
            default:
                return super.onKeyDown(readerDataHolder,keyCode,event);
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
    public boolean onTouchEvent(ReaderDataHolder readerDataHolder, MotionEvent e) {
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
                highlightAlongTouchMoved(readerDataHolder,x, y, cursorSelected);
                return true;
            default:
                break;
        }
        return super.onTouchEvent(readerDataHolder, e);
    }

    public void highlightAlongTouchMoved(ReaderDataHolder readerDataHolder, float x, float y, int cursorSelected) {
        ReaderSelection selection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
        if (selection == null){
            return;
        }
        PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getPageInfo(selection.getPagePosition());
        if (hitTestPage(readerDataHolder,x, y) != pageInfo) {
            return;
        }
        if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
            PointF beginTop = new PointF(x, y);
            PointF endBottom = RectUtils.getEndBottom(selection.getRectangles());
            new SelectWordAction(pageInfo.getName(), beginTop, endBottom, true, this).execute(readerDataHolder);
        } else {
            PointF beginTop = RectUtils.getBeginTop(selection.getRectangles());
            PointF endBottom = new PointF(x, y);
            new SelectWordAction(pageInfo.getName(), beginTop, endBottom, true, this).execute(readerDataHolder);
        }
    }

    private PageInfo hitTestPage(ReaderDataHolder readerDataHolder, float x, float y) {
        if (readerDataHolder.getReaderViewInfo().getVisiblePages() == null) {
            return null;
        }
        for (PageInfo pageInfo : readerDataHolder.getReaderViewInfo().getVisiblePages()) {
            if (pageInfo.getDisplayRect().contains(x, y)) {
                return pageInfo;
            }
        }
        return null;
    }

    private boolean isTopToBottom(PointF leftTop,PointF bottomRight){
        return bottomRight.y >= leftTop.y;
    }

    public void selectWord(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2, boolean b) {
        PageInfo page = hitTestPage(readerDataHolder,x1, y1);
        if (page == null) {
            return;
        }
        new SelectWordAction(page.getName(), new PointF(x1, y1), new PointF(x2, y2),false, this).execute(readerDataHolder);
    }

    public void highlight(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        ShowTextSelectionMenuAction.hideTextSelectionPopupWindow(readerDataHolder, false);
    }

    public void highlightFinished(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2,boolean touchMoved) {
        showHighlightSelectionDialog(readerDataHolder, (int)x1, (int)y1, touchMoved ? PopupSelectionMenu.SelectionType.MultiWordsType : PopupSelectionMenu.SelectionType.SingleWordType);
    }

    private void showHighlightSelectionDialog(ReaderDataHolder readerDataHolder, int x, int y, PopupSelectionMenu.SelectionType type) {
        if (!readerDataHolder.getReaderUserDataInfo().hasHighlightResult()) {
            return;
        }
        new ShowTextSelectionMenuAction(readerDataHolder, x, y, type).execute(readerDataHolder);
    }

    public boolean hasSelectionWord(ReaderDataHolder readerDataHolder) {
        return readerDataHolder.getReaderUserDataInfo().hasHighlightResult();
    }

    public int getCursorSelected(ReaderDataHolder readerDataHolder, int x, int y) {
        HighlightCursor beginHighlightCursor = readerDataHolder.getSelectionManager().getHighlightCursor(HighlightCursor.BEGIN_CURSOR_INDEX);
        if (beginHighlightCursor != null && beginHighlightCursor.hitTest(x, y)) {
            return HighlightCursor.BEGIN_CURSOR_INDEX;
        }

        HighlightCursor endHighlightCursor = readerDataHolder.getSelectionManager().getHighlightCursor(HighlightCursor.END_CURSOR_INDEX);
        if (endHighlightCursor != null && endHighlightCursor.hitTest(x, y)) {
            return HighlightCursor.END_CURSOR_INDEX;
        }
        return -1;
    }

    public void quitWordSelection(ReaderDataHolder readerDataHolder) {
        ShowTextSelectionMenuAction.hideTextSelectionPopupWindow(readerDataHolder,true);
        getParent().resetToDefaultProvider();
        readerDataHolder.redrawPage();
    }

    @Override
    public void onSelectWordFinished(ReaderDataHolder readerDataHolder, SelectWordRequest request, Throwable e, boolean touchMoved) {
        if (e != null) {
            return;
        }

        if (!request.getReaderUserDataInfo().hasHighlightResult()) {
            //Toast.makeText(ReaderActivity.this, R.string.emptyselection, Toast.LENGTH_SHORT).show();
            return;
        }

        ReaderSelection selection = request.getReaderUserDataInfo().getHighlightResult();
        Debug.d(TAG, "select word result: " + JSON.toJSONString(selection));
        readerDataHolder.getSelectionManager().setCurrentSelection(selection);
        readerDataHolder.getSelectionManager().update(readerDataHolder.getContext());
        readerDataHolder.getSelectionManager().updateDisplayPosition();

//        getParent().setActiveProvider(HandlerManager.WORD_SELECTION_PROVIDER);
        readerDataHolder.onRenderRequestFinished(request, e);

        showHighlightSelectionDialog(readerDataHolder,(int)request.getEnd().x, (int)request.getEnd().y, touchMoved ? PopupSelectionMenu.SelectionType.MultiWordsType : PopupSelectionMenu.SelectionType.SingleWordType);
    }
}
