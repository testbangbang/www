package com.onyx.kreader.note.request;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.ShapeUtils;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNotePage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhuzeng on 9/30/16.
 */

public class RemoveShapesByTouchPointListRequest extends ReaderBaseNoteRequest {

    private volatile ReaderBitmapReferenceImpl docBitmap;
    private volatile TouchPointList touchPointList;
    private volatile List<Shape> stash = new ArrayList<>();
    private volatile SurfaceView surfaceView;

    public RemoveShapesByTouchPointListRequest(ReaderBitmapReferenceImpl bitmap, final List<PageInfo> pageInfoList, final TouchPointList pointList, List<Shape> s, SurfaceView view) {
        docBitmap = bitmap;
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

        boolean dirty = false;
        HashMap<String, List<Shape>> removedShapes = new HashMap<>();

        for (PageInfo pageInfo : getVisiblePages()) {
            List<TouchPointList> normalizedList = normalizeOnPage(pageInfo);

            final float radius = noteManager.getNoteDrawingArgs().eraserRadius / pageInfo.getActualScale();
            final ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(),
                    pageInfo.getRange(), pageInfo.getSubPage());
            if (notePage != null) {
                ArrayList<Shape> pageShapes = new ArrayList<>();
                for (TouchPointList list : normalizedList) {
                     pageShapes.addAll(notePage.removeShapesByTouchPointList(list, radius));
                }
                if (pageShapes.size() > 0) {
                    dirty = true;
                    removedShapes.put(notePage.getSubPageUniqueId(), pageShapes);
                }
            }
        }

        HashMap<String, RectF> dirtyRect = new HashMap<>();
        for (Map.Entry<String, List<Shape>> entry : removedShapes.entrySet()) {
            dirtyRect.put(entry.getKey(), ShapeUtils.getBoundingRect(entry.getValue()));
        }
        for (PageInfo pageInfo : getVisiblePages()) {
            final ReaderNotePage notePage = noteManager.getNoteDocument().loadPage(getContext(),
                    pageInfo.getRange(), pageInfo.getSubPage());
            if (notePage != null) {
                if (!dirtyRect.containsKey(notePage.getSubPageUniqueId())) {
                    dirtyRect.put(notePage.getSubPageUniqueId(), new RectF());
                }
            }
        }

        noteManager.setRenderBitmapDirty(dirty);
        getNoteDataInfo().setContentRendered(renderVisiblePages(noteManager, docBitmap, dirtyRect));
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
        EpdController.enablePost(surfaceView, 1);
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
        canvas.drawBitmap(docBitmap.getBitmap(), 0, 0, paint);
        Bitmap bitmap = noteManager.getRenderBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }
}
