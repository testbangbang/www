package com.onyx.edu.reader.ui.handler;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.ui.actions.GotoPositionAction;
import com.onyx.edu.reader.ui.actions.NextScreenAction;
import com.onyx.edu.reader.ui.actions.PanAction;
import com.onyx.edu.reader.ui.actions.PinchZoomAction;
import com.onyx.edu.reader.ui.actions.PreviousScreenAction;
import com.onyx.edu.reader.ui.actions.ShowAnnotationEditDialogAction;
import com.onyx.edu.reader.ui.actions.ShowReaderMenuAction;
import com.onyx.edu.reader.ui.data.BookmarkIconFactory;
import com.onyx.edu.reader.ui.data.PageTurningDetector;
import com.onyx.edu.reader.ui.data.PageTurningDirection;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.events.ClosePopupEvent;
import com.onyx.edu.reader.ui.events.QuitEvent;

import java.util.List;

/**
 * Created by ming on 16/7/27.
 */
public abstract class BaseHandler {

    public static class HandlerInitialState {
        public String ttsInitialPosition;
        public RelativeLayout slideShowParentLayout;
        public int slideShowMaxPageCount;
        public int slideShowIntervalInSeconds;
        public List<View> formFieldControls;
    }

    public static  final int KEYCDOE_SCRIBE = 213;
    public static  final int KEYCDOE_ERASE = 214;
    public static  final int KEYCDOE_SCRIBE_KK = 226;
    public static  final int KEYCDOE_ERASE_KK = 227;

    private Point startPoint = new Point();
    private HandlerManager parent;
    private boolean longPress = false;
    private boolean singleTapUp = false;
    private boolean actionUp = false;
    private boolean pinchZooming = false;
    private boolean scrolling = false;
    private boolean disablePinchZoom = true;


    public boolean isSingleTapUp() {
        return singleTapUp;
    }

    public void setSingleTapUp(boolean singleTapUp) {
        this.singleTapUp = singleTapUp;
    }

    public void setActionUp(boolean actionUp) {
        this.actionUp = actionUp;
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

    public void onActivate(final ReaderDataHolder readerDataHolder, final HandlerInitialState initialState) {}

    public void onDeactivate(final ReaderDataHolder readerDataHolder) {}

    public boolean onDown(ReaderDataHolder readerDataHolder, MotionEvent e) {
        startPoint = new Point((int)e.getX(), (int)e.getY());
        actionUp = false;
        singleTapUp = false;
        return true;
    }

    public boolean onKeyDown(ReaderDataHolder readerDataHolder,int keyCode, KeyEvent event) {
        return false;
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

    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return false;
    }

    public boolean onScrollAfterLongPress(ReaderDataHolder readerDataHolder, float x1, float y1, float x2, float y2) {
        return false;
    }

    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void beforeProcessKeyDown(final ReaderDataHolder readerDataHolder, final String action, final String args) {}

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
        action.execute(readerDataHolder, null);
    }

    public void nextScreen(ReaderDataHolder readerDataHolder, BaseCallback callback) {
        readerDataHolder.setPreRenderNext(true);
        final NextScreenAction action = new NextScreenAction();
        action.execute(readerDataHolder, callback);
    }

    public void prevScreen(ReaderDataHolder readerDataHolder) {
        readerDataHolder.setPreRenderNext(false);
        final PreviousScreenAction action = new PreviousScreenAction();
        action.execute(readerDataHolder, null);
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
        panAction.execute(readerDataHolder, null);
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return tryHitTest(readerDataHolder,e.getX(), e.getY());
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        if (isSkipPinchZooming(readerDataHolder)) {
            return true;
        }
        PinchZoomAction.scaleEnd(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
        return true;
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        if (isSkipPinchZooming(readerDataHolder)) {
            Toast.makeText(readerDataHolder.getContext(), R.string.pinch_zooming_can_not_be_used, Toast.LENGTH_SHORT).show();
            return true;
        }
        setPinchZooming(true);
        PinchZoomAction.scaleBegin(readerDataHolder, detector);
        return true;
    }

    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector)  {
        if (isSkipPinchZooming(readerDataHolder)) {
            return true;
        }
        PinchZoomAction.scaling(readerDataHolder, detector);
        return true;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        if (isLongPress()) {
        } else if (isScrolling() && !isPinchZooming()) {
            panFinished(readerDataHolder,(int) (getStartPoint().x - endX), (int) (getStartPoint().y - endY));
        }
        resetState();
        return true;
    }

    public boolean onActionCancel(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        return true;
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isPinchZooming()) {
            // scrolling may happens after pinch zoom, so we always reset scroll state to avoid conflicts
            setScrolling(false);
            return true;
        }
        setScrolling(true);
        getStartPoint().set((int)e1.getX(), (int)e1.getY());
        if (e2.getAction() == MotionEvent.ACTION_MOVE) {
            panning(readerDataHolder,(int)(e2.getX() - getStartPoint().x), (int)(e2.getY() - getStartPoint().y));
        }
        return true;
    }

    public void onLongPress(ReaderDataHolder readerDataHolder, final float x1, final float y1, final float x2, final float y2) {
        setLongPress(true);
        actionUp = false;
    }

    public void panning(ReaderDataHolder readerDataHolder,int offsetX, int offsetY) {
        if (!readerDataHolder.getReaderViewInfo().canPan()) {
            return;
        }

        PanAction.panning(readerDataHolder, offsetX, offsetY);
    }

    public void panFinished(ReaderDataHolder readerDataHolder,int offsetX, int offsetY) {
        if (!readerDataHolder.getReaderViewInfo().canPan()) {
            PageTurningDirection direction = PageTurningDetector.detectHorizontalTuring(readerDataHolder.getContext(), -offsetX);
            if (direction == PageTurningDirection.Left) {
                beforePageChangeByUser();
                prevPage(readerDataHolder);
            } else if (direction == PageTurningDirection.Right) {
                beforePageChangeByUser();
                nextPage(readerDataHolder);
            }
            return;
        }

        final PanAction panAction = new PanAction(offsetX, offsetY);
        panAction.execute(readerDataHolder, null);
    }

    public boolean tryHitTest(ReaderDataHolder readerDataHolder,float x, float y) {
        if (ShowReaderMenuAction.isReaderMenuShown()) {
            ShowReaderMenuAction.hideReaderMenu();
            return true;
        }
        if (tryBookmark(readerDataHolder,x, y)) {
            return true;
        }
        if (tryAnnotation(readerDataHolder,x, y)) {
            return true;
        }
        if (tryPageLink(readerDataHolder, x, y)) {
            return true;
        }
        return false;
    }

    private boolean tryBookmark(ReaderDataHolder readerDataHolder,final float x, final float y) {
        Bitmap bitmap = BookmarkIconFactory.getBookmarkIcon(readerDataHolder.getContext(), readerDataHolder.hasBookmark());
        final Point point = bookmarkPosition(readerDataHolder,bitmap);
        final int margin = bitmap.getWidth() / 4;
        boolean hit = (x >= point.x - margin && x < point.x + bitmap.getWidth() + margin &&
                y >= point.y - margin && y < point.y + bitmap.getHeight() + margin);
        if (hit) {
            getParent().toggleBookmark(readerDataHolder);
        }
        return hit;
    }

    public boolean tryAnnotation(ReaderDataHolder readerDataHolder,final float x, final float y) {
        for (PageInfo pageInfo : readerDataHolder.getReaderViewInfo().getVisiblePages()) {
            if (!readerDataHolder.getReaderUserDataInfo().hasPageAnnotations(pageInfo)) {
                continue;
            }

            List<PageAnnotation> annotations = readerDataHolder.getReaderUserDataInfo().getPageAnnotations(pageInfo);
            for (PageAnnotation annotation : annotations) {
                for (RectF rect : annotation.getRectangles()) {
                    if (rect.contains(x, y)) {
                        new ShowAnnotationEditDialogAction(annotation.getAnnotation()).execute(readerDataHolder, null);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean tryPageLink(ReaderDataHolder readerDataHolder, final float x, final float y) {
        for (PageInfo pageInfo : readerDataHolder.getReaderViewInfo().getVisiblePages()) {
            if (!readerDataHolder.getReaderUserDataInfo().hasPageLinks(pageInfo)) {
                continue;
            }
            List<ReaderSelection> links = readerDataHolder.getReaderUserDataInfo().getPageLinks(pageInfo);
            for (ReaderSelection link : links) {
                for (RectF rect : link.getRectangles()) {
                    if (rect.contains(x, y)) {
                        new GotoPositionAction(link.getPagePosition()).execute(readerDataHolder);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Point bookmarkPosition(ReaderDataHolder readerDataHolder,Bitmap bitmap) {
        Point point = new Point();
        point.set(readerDataHolder.getDisplayWidth() - bitmap.getWidth(), 10);
        return point;
    }

    public void beforePageChangeByUser() {
    }

    public void showReaderMenu(ReaderDataHolder readerDataHolder) {
        new ShowReaderMenuAction().execute(readerDataHolder, null);
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(boolean s) {
        scrolling = s;
    }

    public void setPinchZooming(boolean s) {
        pinchZooming = s;
    }

    public boolean isPinchZooming() {
        return pinchZooming;
    }

    public boolean isSkipPinchZooming(final ReaderDataHolder readerDataHolder) {
        if (disablePinchZoom) {
            return true;
        }
        return (readerDataHolder.isFixedPageDocument() && !readerDataHolder.supportScalable()) ||
                (readerDataHolder.isFlowDocument() && !readerDataHolder.supportFontSizeAdjustment());
    }

    public void resetState() {
        scrolling = false;
        pinchZooming = false;
    }

    public void close(final ReaderDataHolder readerDataHolder) {
        if (ShowReaderMenuAction.isReaderMenuShown()) {
            readerDataHolder.getEventBus().post(new ClosePopupEvent());
            return;
        }
        readerDataHolder.getEventBus().post(new QuitEvent());
    }
}
