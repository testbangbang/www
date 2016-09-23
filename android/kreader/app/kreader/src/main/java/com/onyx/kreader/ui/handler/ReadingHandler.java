package com.onyx.kreader.ui.handler;


import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.common.PageAnnotation;
import com.onyx.kreader.ui.actions.NextScreenAction;
import com.onyx.kreader.ui.actions.PanAction;
import com.onyx.kreader.ui.actions.PinchZoomAction;
import com.onyx.kreader.ui.actions.PreviousScreenAction;
import com.onyx.kreader.ui.actions.ShowAnnotationEditDialogAction;
import com.onyx.kreader.ui.actions.ShowReaderMenuAction;
import com.onyx.kreader.ui.actions.ToggleBookmarkAction;
import com.onyx.kreader.ui.data.BookmarkIconFactory;
import com.onyx.kreader.ui.data.PageTurningDetector;
import com.onyx.kreader.ui.data.PageTurningDirection;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: zhuzeng
 * Date: 3/19/14
 * Time: 8:42 PM
 * Basic event handler.
 */
public class ReadingHandler extends BaseHandler{

    @SuppressWarnings("unused")
    private static final String TAG = ReadingHandler.class.getSimpleName();

    private boolean scaling = false;
    private boolean scrolling = false;

    public ReadingHandler(HandlerManager p) {
        super(p);
    }

    public boolean onSingleTapUp(ReaderDataHolder readerDataHolder, MotionEvent e) {
        if (tryHitTest(readerDataHolder,e.getX(), e.getY())) {
            // ignored
        } else if (e.getX() > readerDataHolder.getDisplayWidth() * 2 / 3) {
            nextScreen(readerDataHolder);
        } else if (e.getX() < readerDataHolder.getDisplayWidth() / 3) {
            prevScreen(readerDataHolder);
        } else {
            showReaderMenu(readerDataHolder);
        }
        return true;
    }

    public boolean onSingleTapConfirmed(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTap(ReaderDataHolder readerDataHolder, MotionEvent e) {
        return true;
    }

    public boolean onScaleEnd(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        PinchZoomAction.scaleEnd(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                setScaling(false);
            }
        });
        return true;
    }

    public boolean onScaleBegin(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        setScaling(true);
        PinchZoomAction.scaleBegin(readerDataHolder, detector);
        return true;
    }

    public boolean onScale(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector)  {
        PinchZoomAction.scaling(readerDataHolder, detector);
        return true;
    }

    public boolean onActionUp(ReaderDataHolder readerDataHolder, final float startX, final float startY, final float endX, final float endY) {
        if (isLongPress()) {
        } else if (isScrolling() && !isScaling()) {
            panFinished(readerDataHolder,(int) (getStartPoint().x - endX), (int) (getStartPoint().y - endY));
        }
        resetState();
        return true;
    }

    public boolean onScroll(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (isScaling()) {
            // scrolling may happens before scale, so we always reset scroll state to avoid conflicts
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
    }

    public boolean onFling(ReaderDataHolder readerDataHolder, MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
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

    public void setScaling(boolean s) {
        scaling = s;
    }

    public boolean isScaling() {
        return scaling;
    }

    public void resetState() {
        scrolling = false;
    }

    public void setPenErasing(boolean c) {
        getParent().setPenErasing(c);
    }

    public boolean isPenErasing() {
        return getParent().isPenErasing();
    }

    public void setPenStart(boolean s) {
        getParent().setPenStart(s);
    }

    public boolean isPenStart() {
        return getParent().isPenStart();
    }

}
