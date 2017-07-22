package com.onyx.android.dr.reader.handler;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.dr.reader.action.SelectWordAction;
import com.onyx.android.dr.reader.action.ShowTextSelectionMenuAction;
import com.onyx.android.dr.reader.dialog.DialogAnnotation;
import com.onyx.android.dr.reader.dialog.ReaderDialogManage;
import com.onyx.android.dr.reader.highlight.HighlightCursor;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;
import com.onyx.android.sdk.reader.host.request.SelectWordRequest;
import com.onyx.android.sdk.utils.MathUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/23/14
 * Time: 11:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class WordSelectionHandler extends BaseHandler {

    private static final String TAG = WordSelectionHandler.class.getSimpleName();
    private static final int MAX_WORD_CHINESE_COUNT = 10;

    private int moveRangeAfterLongPress = 10;

    private float movePointOffsetHeight;
    private Point lastMovedPoint = null;
    private int cursorSelected = -1;
    private boolean showSelectionCursor = true;
    private boolean movedAfterLongPress = false;
    private Point longPressPoint = new Point();
    private SelectWordRequest selectWordRequest;
    private PointF highLightBeginTop;
    private PointF highLightEndBottom;
    private int lastSelectStartPosition = 0;
    private int lastSelectEndPosition = 0;

    public WordSelectionHandler(ReaderPresenter readerPresenter) {
        super(readerPresenter);
    }

    @Override
    public void onLongPress(MotionEvent event) {
        if (tryPageImage(event.getX(), event.getY())) {
            quitWordSelection();
            return;
        }
        longPressPoint.set((int) event.getX(), (int) event.getY());
        lastMovedPoint = new Point((int) event.getX(), (int) event.getY());
        cursorSelected = getCursorSelected((int) event.getX(), (int) event.getY());
        boolean has = hasSelectionWord();
        if (!has) {
            highLightBeginTop = new PointF(event.getX(), event.getY());
            highLightEndBottom = new PointF(event.getX(), event.getY());
            movedAfterLongPress = false;
            showSelectionCursor = false;
            lastSelectStartPosition = 0;
            lastSelectEndPosition = 0;
            selectWord(getStartPoint().x, getStartPoint().y, event.getX(), event.getY());
        } else if (cursorSelected < 0) {
            quitWordSelection();
        }
    }

    private boolean tryPageImage(final float x, final float y) {
        return false;
    }

    public boolean onDown(MotionEvent event) {
        cursorSelected = getCursorSelected((int) event.getX(), (int) event.getY());
        return super.onDown(event);
    }

    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
        return true;
    }

    public boolean onSingleTapConfirmed(MotionEvent event) {
        quitWordSelection();
        return true;
    }

    @Override
    public boolean onActionUp(MotionEvent event) {
        onReleaseClick();
        return true;
    }

    @Override
    public boolean onActionCancel(MotionEvent event) {
        onReleaseClick();
        return true;
    }

    public void onReleaseClick() {
        if (getReaderPresenter().getReaderUserDataInfo().hasHighlightResult()) {
            String text = getReaderPresenter().getReaderUserDataInfo().getHighlightResult().getText();
            if (!StringUtils.isNullOrEmpty(text)) {
                ReaderDialogManage.onShowMainMenu(getReaderPresenter(), true);
            }
        }
        updateHighLightRect();
    }

    private boolean isWord(String text) {
        text = text.trim();
        boolean isAlphaWord = ReaderTextSplitterImpl.isAlpha(text.charAt(0));
        if (isAlphaWord) {
            return !text.contains(" ");
        } else {
            return text.length() <= MAX_WORD_CHINESE_COUNT;
        }
    }

    private void updateHighLightRect() {
        if (getReaderPresenter().getReaderUserDataInfo().hasHighlightResult()) {
            final ReaderSelection selection = getReaderPresenter().getReaderUserDataInfo().getHighlightResult();
            if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
                highLightBeginTop = RectUtils.getBeginTop(selection.getRectangles());
            } else {
                highLightEndBottom = RectUtils.getEndBottom(selection.getRectangles());
            }
        }
    }

    private void showSelectionMenu(boolean isWord) {
        getReaderPresenter().setPageAnnotation(null);
        ShowTextSelectionMenuAction.showTextSelectionPopupMenu(getReaderPresenter(), false, DialogAnnotation.AnnotationAction.add);
        enableSelectionCursor(selectWordRequest);
    }

    public boolean onScrollAfterLongPress(final float x1, final float y1, final float x2, final float y2) {
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                quitWordSelection();
                return true;
            case KeyEvent.KEYCODE_PAGE_DOWN:
            case KeyEvent.KEYCODE_PAGE_UP:
                quitWordSelection();
                return super.onKeyDown(keyCode, event);
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private boolean moveOutOfRange(int x, int y) {
        return MathUtils.distance(lastMovedPoint.x, lastMovedPoint.y, x, y) > moveRangeAfterLongPress;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (cursorSelected < 0 && showSelectionCursor) {
                    return true;
                }

                if (filterMoveAfterLongPress(x, y)) {
                    return true;
                }

                highlightAlongTouchMoved(x, y, cursorSelected);
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return true;
    }

    private boolean filterMoveAfterLongPress(float x, float y) {
        if (!movedAfterLongPress) {
            if (!moveOutOfRange((int) x, (int) y)) {
                return true;
            }
        }
        movedAfterLongPress = true;
        return false;
    }

    public void close() {
        quitWordSelection();
    }

    public void highlightAlongTouchMoved(float x, float y, int cursorSelected) {
        hideTextSelectionPopupWindow();
        selectText(longPressPoint.x, longPressPoint.y, x, y);
    }

    public void selectWord(final float x1, final float y1, final float x2, final float y2) {
        SelectWordAction.selectWord(getReaderPresenter(),
                getReaderPresenter().getCurrentPagePosition(),
                new PointF(x1, y1),
                new PointF(x2, y2),
                new PointF(x2, y2),
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onSelectWordFinished((SelectWordRequest) request, e);
                    }
                });
    }

    public void selectText(final float x1, final float y1, final float x2, final float y2) {
        PointF touchPoint = new PointF(x2, y2);
        final ReaderSelection selection = getReaderPresenter().getReaderUserDataInfo().getHighlightResult();
        if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
            highLightBeginTop = new PointF(x2, y2 - getMovePointOffsetHeight());
        } else {
            highLightEndBottom = new PointF(x2, y2 - getMovePointOffsetHeight());
        }

        SelectWordAction.selectText(getReaderPresenter(),
                getReaderPresenter().getCurrentPagePosition(),
                highLightBeginTop,
                highLightEndBottom,
                touchPoint,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        onSelectWordFinished((SelectWordRequest) request, e);
                        updateCursorSelected(selection);
                    }
                });
    }

    private float getMovePointOffsetHeight() {
        if (!getReaderPresenter().getReaderSelectionManager().isEnable()) {
            return 0f;
        }
        return movePointOffsetHeight;
    }

    private void updateCursorSelected(ReaderSelection selection) {
        final ReaderSelection updatedSelection = getReaderPresenter().getReaderUserDataInfo().getHighlightResult();
        if (updatedSelection == null || selection == null) {
            return;
        }

        if (cursorSelected == HighlightCursor.END_CURSOR_INDEX) {
            int selectStartPosition = Integer.valueOf(selection.getStartPosition());
            if (lastSelectStartPosition != 0 && lastSelectStartPosition != selectStartPosition) {
                highLightEndBottom.set(highLightBeginTop.x, highLightBeginTop.y);
                cursorSelected = HighlightCursor.BEGIN_CURSOR_INDEX;
            }
            lastSelectStartPosition = selectStartPosition;
        } else if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
            int selectEndPosition = Integer.valueOf(selection.getEndPosition());
            if (lastSelectEndPosition != 0 && lastSelectEndPosition != selectEndPosition) {
                highLightBeginTop.set(highLightEndBottom.x, highLightEndBottom.y);
                cursorSelected = HighlightCursor.END_CURSOR_INDEX;
            }
            lastSelectEndPosition = selectEndPosition;
        }
    }

    public void hideTextSelectionPopupWindow() {
        ShowTextSelectionMenuAction.hideTextSelectionPopupWindow(getReaderPresenter(), false);
    }

    public boolean hasSelectionWord() {
        return getReaderPresenter().getReaderUserDataInfo().hasHighlightResult();
    }

    public int getCursorSelected(int x, int y) {
        HighlightCursor beginHighlightCursor = getReaderPresenter().getReaderSelectionManager().getHighlightCursor(HighlightCursor.BEGIN_CURSOR_INDEX);
        HighlightCursor endHighlightCursor = getReaderPresenter().getReaderSelectionManager().getHighlightCursor(HighlightCursor.END_CURSOR_INDEX);

        if (endHighlightCursor != null && endHighlightCursor.hitTest(x, y)) {
            return HighlightCursor.END_CURSOR_INDEX;
        }
        if (beginHighlightCursor != null && beginHighlightCursor.hitTest(x, y)) {
            return HighlightCursor.BEGIN_CURSOR_INDEX;
        }
        return -1;
    }

    public void quitWordSelection() {
        clearWordSelection();
    }

    private void clearWordSelection() {
        ShowTextSelectionMenuAction.hideTextSelectionPopupWindow(getReaderPresenter(), true);
        getReaderPresenter().getBookOperate().redrawPage();
        getReaderPresenter().getHandlerManger().updateActionProviderType(HandlerManger.READING_PROVIDER);
        getReaderPresenter().getReaderSelectionManager().clear();
    }

    public void onSelectWordFinished(SelectWordRequest request, Throwable e) {
        if (e != null) {
            return;
        }
        if (request.getReaderUserDataInfo().hasHighlightResult()) {
            ReaderSelection selection = request.getReaderUserDataInfo().getHighlightResult();
            getReaderPresenter().getReaderSelectionManager().setCurrentSelection(selection);
            getReaderPresenter().getReaderSelectionManager().update(getReaderPresenter().getReaderView().getViewContext());
            getReaderPresenter().getReaderSelectionManager().updateDisplayPosition();
            getReaderPresenter().getReaderSelectionManager().setEnable(showSelectionCursor);
            selectWordRequest = request;
            getReaderPresenter().onRenderRequestFinished(request, e, false, false);
        }
        onReleaseClick();
    }

    private void enableSelectionCursor(BaseReaderRequest request) {
        if (request == null) {
            return;
        }
        getReaderPresenter().getReaderSelectionManager().setEnable(true);
        getReaderPresenter().onRenderRequestFinished(request, null, false, false);
        showSelectionCursor = true;
    }
}
