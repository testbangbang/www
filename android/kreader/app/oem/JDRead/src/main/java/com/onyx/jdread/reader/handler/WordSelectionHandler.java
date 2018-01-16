package com.onyx.jdread.reader.handler;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.device.ReaderDeviceManager;
import com.onyx.android.sdk.reader.host.impl.ReaderTextSplitterImpl;
import com.onyx.android.sdk.utils.MathUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.actions.NextPageSelectTextAction;
import com.onyx.jdread.reader.actions.PrevPageSelectTextAction;
import com.onyx.jdread.reader.actions.SelectTextAction;
import com.onyx.jdread.reader.actions.SelectWordAction;
import com.onyx.jdread.reader.actions.UpdateViewPageAction;
import com.onyx.jdread.reader.common.SelectWordInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.highlight.HighlightCursor;

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

    private int moveRangeAfterLongPress = 15;

    private float movePointOffsetHeight;
    private Point lastMovedPoint = null;
    private int cursorSelected = -1;
    private boolean showSelectionCursor = true;
    private boolean movedAfterLongPress = false;
    private Point longPressPoint = new Point();
    private PointF highLightBeginTop;
    private PointF highLightEndBottom;
    private int lastSelectStartPosition = 0;
    private int lastSelectEndPosition = 0;
    private float lastMoveX = 0;
    private float lastMoveY = 0;
    private int crossScreenTouchRegionMinWidth;
    private int crossScreenTouchRegionMinHeight;
    private String pagePosition;

    public WordSelectionHandler(ReaderDataHolder readerDataHolder) {
        super(readerDataHolder);
        movePointOffsetHeight = readerDataHolder.getAppContext().getResources().getDimension(R.dimen.move_point_offset_height);
        crossScreenTouchRegionMinWidth = readerDataHolder.getAppContext().getResources().getInteger(R.integer.reader_cross_screen_touch_region_min_width);
        crossScreenTouchRegionMinHeight = readerDataHolder.getAppContext().getResources().getInteger(R.integer.reader_cross_screen_touch_region_min_height);
    }

    @Override
    public void onLongPress(MotionEvent event) {
        pagePosition = getReaderDataHolder().getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
        if (isCrossScreenSelectText(event)) {
            return;
        }
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

    private boolean isCrossScreenSelectText(MotionEvent event) {
        if (getReaderDataHolder().getReaderSelectionManager().getCurrentSelection(pagePosition) != null) {
            int height = getReaderDataHolder().getReader().getReaderViewHelper().getPageViewHeight();
            int width = getReaderDataHolder().getReader().getReaderViewHelper().getPageViewWidth();
            float x = event.getX();
            float y = event.getY();
            if (x < crossScreenTouchRegionMinWidth && y < crossScreenTouchRegionMinHeight) {
                if (getReaderDataHolder().getReaderViewInfo().canPrevScreen) {
                    new PrevPageSelectTextAction().execute(getReaderDataHolder());
                }
                return true;
            }
            if (x > (width - crossScreenTouchRegionMinWidth) && y > (height - crossScreenTouchRegionMinHeight)) {
                if (getReaderDataHolder().getReaderViewInfo().canNextScreen) {
                    new NextPageSelectTextAction().execute(getReaderDataHolder());
                }
                return true;
            }
        }
        return false;
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
        if (getReaderDataHolder().getReaderUserDataInfo().hasHighlightResult()) {
            String text = getReaderDataHolder().getReaderUserDataInfo().getHighlightResult().getText();
            if (!StringUtils.isNullOrEmpty(text)) {
                boolean isWord = isWord(text);
                showSelectionMenu(isWord);
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
        if (getReaderDataHolder().getReaderUserDataInfo().hasHighlightResult()) {
            final ReaderSelection selection = getReaderDataHolder().getReaderUserDataInfo().getHighlightResult();
            if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
                highLightBeginTop = RectUtils.getBeginTop(selection.getRectangles());
            } else {
                highLightEndBottom = RectUtils.getEndBottom(selection.getRectangles());
            }
        }
    }

    private void showSelectionMenu(boolean isWord) {
        enableSelectionCursor();
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
                if (Math.abs(lastMoveX - x) <= 0 || Math.abs(lastMoveY - y) <= 0) {
                    return true;
                }
                lastMoveX = x;
                lastMoveY = y;
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
        SelectWordInfo info = new SelectWordInfo(getReaderDataHolder().getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition(),
                new PointF(x1, y1),
                new PointF(x2, y2),
                new PointF(x2, y2));
        SelectWordAction action = new SelectWordAction(info);
        action.execute(getReaderDataHolder());
    }

    public void selectText(final float x1, final float y1, final float x2, final float y2) {
        PointF touchPoint = new PointF(x2, y2);
        if (cursorSelected == HighlightCursor.BEGIN_CURSOR_INDEX) {
            highLightBeginTop = new PointF(x2, y2 - getMovePointOffsetHeight());
        } else {
            highLightEndBottom = new PointF(x2, y2 - getMovePointOffsetHeight());
        }

        SelectWordInfo info = new SelectWordInfo(getReaderDataHolder().getReader().getReaderHelper().getReaderLayoutManager().getCurrentPagePosition(),
                highLightBeginTop,
                highLightEndBottom,
                touchPoint);
        SelectTextAction action = new SelectTextAction(info);
        action.execute(getReaderDataHolder());
    }

    private float getMovePointOffsetHeight() {
        if (!getReaderDataHolder().getReaderSelectionManager().isEnable(pagePosition)) {
            return 0f;
        }
        return movePointOffsetHeight;
    }

    private void updateCursorSelected(ReaderSelection selection) {
        final ReaderSelection updatedSelection = getReaderDataHolder().getReaderUserDataInfo().getHighlightResult();
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
    }

    public boolean hasSelectionWord() {
        return getReaderDataHolder().getReaderUserDataInfo().hasHighlightResult();
    }

    public int getCursorSelected(int x, int y) {
        HighlightCursor beginHighlightCursor = getReaderDataHolder().getReaderSelectionManager().getHighlightCursor(pagePosition,HighlightCursor.BEGIN_CURSOR_INDEX);
        HighlightCursor endHighlightCursor = getReaderDataHolder().getReaderSelectionManager().getHighlightCursor(pagePosition,HighlightCursor.END_CURSOR_INDEX);

        if (endHighlightCursor != null && endHighlightCursor.hitTest(x, y)) {
            return HighlightCursor.END_CURSOR_INDEX;
        }
        if (beginHighlightCursor != null && beginHighlightCursor.hitTest(x, y)) {
            return HighlightCursor.BEGIN_CURSOR_INDEX;
        }
        return -1;
    }

    public void quitWordSelection() {
        ReaderDeviceManager.enableRegal();
        clearWordSelection();
    }

    private void clearWordSelection() {
        new UpdateViewPageAction().execute(getReaderDataHolder());
        getReaderDataHolder().getHandlerManger().updateActionProviderType(HandlerManger.READING_PROVIDER);
        getReaderDataHolder().getReaderSelectionManager().clear(pagePosition);
    }

    private void enableSelectionCursor() {
        showSelectionCursor = true;
    }
}
