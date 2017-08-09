package com.onyx.android.dr.reader.handler;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.dr.reader.action.ShowReaderBottomMenuDialogAction;
import com.onyx.android.dr.reader.action.ShowTextSelectionMenuAction;
import com.onyx.android.dr.reader.data.BookmarkIconFactory;
import com.onyx.android.dr.reader.dialog.DialogAnnotation;
import com.onyx.android.dr.reader.dialog.ReaderDialogManage;
import com.onyx.android.dr.reader.presenter.ReaderPresenter;
import com.onyx.android.sdk.data.ControlType;
import com.onyx.android.sdk.data.CustomBindKeyBean;
import com.onyx.android.sdk.data.KeyBinding;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.TouchAction;
import com.onyx.android.sdk.data.TouchBinding;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * Created by huxiaomao on 17/5/8.
 */

public class BaseHandler {
    private static final String TAG = BaseHandler.class.getSimpleName();
    public static final int TOUCH_HORIZONTAL_PART = 3;
    public static final int TOUCH_VERTICAL_PART = 2;
    private ReaderPresenter readerPresenter;
    private static Point startPoint = new Point();

    public BaseHandler(ReaderPresenter readerPresenter) {
        this.readerPresenter = readerPresenter;
    }

    public ReaderPresenter getReaderPresenter() {
        return readerPresenter;
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
        point.set(getReaderPresenter().getPageInformation().getDisplayWidth() - bitmap.getWidth(), 10);
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

    public static String getTouchAreaCode(ReaderPresenter readerPresenter, final MotionEvent event) {
        int displayWidth = readerPresenter.getPageInformation().getDisplayWidth();
        int displayHeight = readerPresenter.getPageInformation().getDisplayHeight();
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

    public static boolean processSingleTapUp(ReaderPresenter readerPresenter, final String action, final String args) {
        if (StringUtils.isNullOrEmpty(action)) {
            return false;
        }
        if (action.equals(TouchAction.NEXT_PAGE)) {
            readerPresenter.nextScreen();
        } else if (action.equals(TouchAction.PREV_PAGE)) {
            readerPresenter.prevScreen();
        } else if (action.equals(TouchAction.SHOW_MENU)) {
            ReaderDialogManage.onShowMainMenu(readerPresenter, StringUtils.isNotBlank(readerPresenter.getBookOperate().getSelectionText()), getAction(readerPresenter));
        } else if (action.equals(TouchAction.INCREASE_BRIGHTNESS)) {
            //increaseBrightness(readerDataHolder);
        } else if (action.equals(TouchAction.DECREASE_BRIGHTNESS)) {
            //decreaseBrightness(readerDataHolder);
        } else if (action.equals(TouchAction.TOGGLE_FULLSCREEN)) {
            //toggleFullscreen(readerDataHolder);
        } else if (action.equals(TouchAction.OPEN_TTS)) {
            //ShowReaderMenuAction.showTtsDialog(readerDataHolder);
        } else if (action.equals(TouchAction.AUTO_PAGE)) {
            //readerDataHolder.enterSlideshow();
        } else if (action.equals(TouchAction.NEXT_TEN_PAGE)) {
            //nextTenPage(readerDataHolder);
        } else if (action.equals(TouchAction.PREV_TEN_PAGE)) {
            //prevTenPage(readerDataHolder);
        } else {
            return false;
        }
        return true;
    }

    private static DialogAnnotation.AnnotationAction getAction(ReaderPresenter readerPresenter) {
        if (StringUtils.isNotBlank(readerPresenter.getBookOperate().getSelectionText())) {
            return DialogAnnotation.AnnotationAction.add;
        } else {
            return DialogAnnotation.AnnotationAction.update;
        }
    }

    public static CustomBindKeyBean getControlBinding(final ControlType controlType, final String controlCode) {
        Map<String, CustomBindKeyBean> map = controlType == ControlType.KEY ? KeyBinding.defaultValue().getHandlerManager() : TouchBinding.defaultValue().getTouchBindingMap();
        if (map == null) {
            return null;
        }
        return map.get(controlCode);
    }

    public void onStop() {

    }

    public boolean tryHitTest(float x, float y) {
        if (ShowTextSelectionMenuAction.isSelectionMenuShow()) {
            ShowTextSelectionMenuAction.hideTextSelectionPopupMenu(readerPresenter);
            readerPresenter.getHandlerManger().updateActionProviderType(HandlerManger.READING_PROVIDER);
            return true;
        }
        if (tryBookmark(readerPresenter, x, y)) {
            return true;
        }
        if (tryAnnotation(x, y)) {
            return true;
        }
        if (tryPageLink(x, y)) {
            return true;
        }
        return false;
    }

    private boolean tryPageLink(final float x, final float y) {
        for (PageInfo pageInfo : getReaderPresenter().getReaderViewInfo().getVisiblePages()) {
            if (!getReaderPresenter().getReaderUserDataInfo().hasPageLinks(pageInfo)) {
                continue;
            }
            List<ReaderSelection> links = getReaderPresenter().getReaderUserDataInfo().getPageLinks(pageInfo);
            for (ReaderSelection link : links) {
                for (RectF rect : link.getRectangles()) {
                    if (rect.contains(x, y)) {
                        getReaderPresenter().getBookOperate().GotoPositionAction(link.getPagePosition(), false);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean tryBookmark(ReaderPresenter readerPresenter, final float x, final float y) {
        Bitmap bitmap = BookmarkIconFactory.getBookmarkIcon(readerPresenter.getReaderView().getViewContext(),
                readerPresenter.hasBookmark());
        final Point point = bookmarkPosition(bitmap);
        final int margin = bitmap.getWidth() / 4;
        boolean hit = (x >= point.x - margin && x < point.x + bitmap.getWidth() + margin &&
                y >= point.y - margin && y < point.y + bitmap.getHeight() + margin);
        if (hit) {
            toggleBookmark();
        }
        return hit;
    }

    public void toggleBookmark() {
        if (getReaderPresenter().hasBookmark()) {
            removeBookmark();
        } else {
            addBookmark();
        }
    }

    private void removeBookmark() {
        readerPresenter.getBookOperate().removeBookmark(null);
    }

    private void addBookmark() {
        readerPresenter.getBookOperate().addBookmark();
    }

    public boolean tryAnnotation(final float x, final float y) {
        for (PageInfo pageInfo : getReaderPresenter().getReaderViewInfo().getVisiblePages()) {
            if (!getReaderPresenter().getReaderUserDataInfo().hasPageAnnotations(pageInfo)) {
                continue;
            }

            List<PageAnnotation> annotations = getReaderPresenter().getReaderUserDataInfo().getPageAnnotations(pageInfo);
            for (PageAnnotation annotation : annotations) {
                for (RectF rect : annotation.getRectangles()) {
                    if (rect.contains(x, y)) {
                        getReaderPresenter().setPageAnnotation(annotation);
                        ReaderDialogManage.onShowMainMenu(readerPresenter, false, DialogAnnotation.AnnotationAction.update);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
