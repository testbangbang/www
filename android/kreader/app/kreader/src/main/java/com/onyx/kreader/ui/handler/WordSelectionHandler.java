package com.onyx.kreader.ui.handler;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.api.ReaderSelection;
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
public class WordSelectionHandler extends BaseHandler{

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
            selectWord(readerDataHolder,x1, y1, x2, y2, false);
        } else {
            highlight(readerDataHolder,x1, y1, x2, y2);
        }
        readerDataHolder.changeEpdUpdateMode(UpdateMode.DU);
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

    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        quitWordSelection(readerDataHolder);
        return true;
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return true;
    }

    public boolean onActionUp(final ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        if (moveAfterLongPress){
            onActionUpAfterTouchMoved(readerDataHolder,startX, startY, endX, endY);
        }else if (hasSelectionWord(readerDataHolder) && isLongPress()){
            selectWord(readerDataHolder,startX, startY, endX, endY, true);
        }
        setLongPress(false);
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
                if (cursorNotSelected) {
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
                break;
            case MotionEvent.ACTION_UP:
                moveAfterLongPress = false;
                break;
            default:
                break;
        }
        return true;
    }

    public void highlightAlongTouchMoved(ReaderDataHolder readerDataHolder, float x, float y, int cursorSelected) {
        handleTouchMovingUpAction(readerDataHolder,x,y,true);
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

    public void selectWord(final ReaderDataHolder readerDataHolder, final float x1, final float y1, float x2, float y2, final boolean show) {
        PageInfo page = hitTestPage(readerDataHolder,x1, y1);
        if (page == null) {
            return;
        }
        SelectWordAction.selectWord(readerDataHolder, page.getName(), new PointF(x1, y1), new PointF(x2, y2), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onSelectWordFinished(readerDataHolder, (SelectWordRequest) request, e, show, PopupSelectionMenu.SelectionType.SingleWordType);
                cursorSelected = getCursorSelected(readerDataHolder,(int)x1, (int)y1);
            }
        });
    }

    public void highlight(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        ShowTextSelectionMenuAction.hideTextSelectionPopupWindow(readerDataHolder, false);
    }

    public void onActionUpAfterTouchMoved(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
        handleTouchMovingUpAction(readerDataHolder,x2,y2,false);
    }

    private void handleTouchMovingUpAction(final ReaderDataHolder readerDataHolder, final float x, final float y, final boolean touchMoving){
        final ReaderSelection selection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
        if (selection == null){
            return;
        }
        PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getPageInfo(selection.getPagePosition());

        PointF beginTop;
        PointF endBottom;
        if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
            beginTop = new PointF(x, y);
            endBottom = RectUtils.getEndBottom(selection.getRectangles());
        } else {
            beginTop = RectUtils.getBeginTop(selection.getRectangles());
            endBottom = new PointF(x, y);
        }

        SelectWordAction.selectWord(readerDataHolder, pageInfo.getName(), beginTop, endBottom, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                onSelectWordFinished(readerDataHolder, (SelectWordRequest)request, e, !touchMoving, PopupSelectionMenu.SelectionType.MultiWordsType);
                final ReaderSelection updatedSelection = readerDataHolder.getReaderUserDataInfo().getHighlightResult();
                if (updatedSelection == null) {
                    return;
                }
                if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
                    if (selection.getEndPosition().equals(updatedSelection.getStartPosition())) {
                        cursorSelected = HighlightCursor.END_CURSOR_INDEX;
                    }
                } else if (cursorSelected == HighlightCursor.END_CURSOR_INDEX) {
                    if (selection.getStartPosition().equals(updatedSelection.getEndPosition())) {
                        cursorSelected = HighlightCursor.BEGIN_CURSOR_INDEX;
                    }
                }
            }
        });
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
        readerDataHolder.resetEpdUpdateMode();
        ShowTextSelectionMenuAction.hideTextSelectionPopupWindow(readerDataHolder,true);
        getParent().resetToDefaultProvider();
        readerDataHolder.redrawPage();
        readerDataHolder.getSelectionManager().clear();
        readerDataHolder.getHandlerManager().setActiveProvider(HandlerManager.READING_PROVIDER);
    }

    public void onSelectWordFinished(ReaderDataHolder readerDataHolder, SelectWordRequest request, Throwable e, boolean show, PopupSelectionMenu.SelectionType selectionType) {
        if (e != null) {
            return;
        }

        if (request.getReaderUserDataInfo().hasHighlightResult()) {
            ReaderSelection selection = request.getReaderUserDataInfo().getHighlightResult();
            readerDataHolder.getSelectionManager().setCurrentSelection(selection);
            readerDataHolder.getSelectionManager().update(readerDataHolder.getContext());
            readerDataHolder.getSelectionManager().updateDisplayPosition();
            readerDataHolder.onRenderRequestFinished(request, e, false);
        }

        if (show){
            new ShowTextSelectionMenuAction(readerDataHolder, (int)request.getEnd().x, (int)request.getEnd().y, selectionType).execute(readerDataHolder);
        }else {
            ShowTextSelectionMenuAction.hideTextSelectionPopupMenu();
        }
    }
}
