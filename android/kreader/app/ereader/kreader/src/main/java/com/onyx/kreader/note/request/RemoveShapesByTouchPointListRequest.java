package com.onyx.kreader.note.request;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 9/30/16.
 */

public class RemoveShapesByTouchPointListRequest extends ReaderBaseNoteRequest {

    private volatile TouchPointList touchPointList;
    private volatile List<Shape> stash = new ArrayList<>();
    private volatile SurfaceView surfaceView;

    public RemoveShapesByTouchPointListRequest(final List<PageInfo> pageInfoList, final TouchPointList pointList, List<Shape> s, SurfaceView view) {
        setVisiblePages(pageInfoList);
        touchPointList = pointList;
        stash.addAll(s);
        surfaceView = view;
        setResetNoteDataInfo(false);
    }

    public void execute(final NoteManager noteManager) throws Exception {
        for(Shape shape : stash) {
            final ReaderNotePage readerNotePage = noteManager.getNoteDocument().ensurePageExist(getContext(), shape.getPageUniqueId(), shape.getSubPageUniqueId());
            if (readerNotePage != null) {
                readerNotePage.addShape(shape, true);
            }
        }

        for (PageInfo pageInfo : getVisiblePages()) {
            List<TouchPointList> normalizedList = normalizeOnPage(pageInfo);

            final float radius = noteManager.getNoteDrawingArgs().eraserRadius / pageInfo.getActualScale();
            final ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(),
                    pageInfo.getRange(), pageInfo.getSubPage());
            if (notePage != null) {
                for (TouchPointList list : normalizedList) {
                    notePage.removeShapesByTouchPointList(list, radius);
                }
            }
        }
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager));
        renderToScreen(noteManager);
        
        setResumeRawInputProcessor(noteManager.isDFBForCurrentShape());
    }

    private List<TouchPointList> normalizeOnPage(PageInfo pageInfo) {
        List<TouchPointList> result = new ArrayList<>();
        TouchPointList currentList = null;
        for (TouchPoint p : touchPointList.getPoints()) {
            if (!pageInfo.getDisplayRect().contains(p.getX(), p.getY())) {
                if (currentList != null) {
                    result.add(currentList);
                }
                currentList = null;
            } else {
                if (currentList == null) {
                    currentList = new TouchPointList();
                }
                TouchPoint pp = new TouchPoint(p);
                pp.normalize(pageInfo);
                currentList.add(pp);
            }
        }
        if (currentList != null) {
            result.add(currentList);
        }
        return result;
    }

    private void renderToScreen(final NoteManager noteManager) {
        EpdController.resetEpdPost();
        if (surfaceView == null) {
            return;
        }
        Rect rect = getViewportSize();
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
        Bitmap bitmap = noteManager.getRenderBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }
}
