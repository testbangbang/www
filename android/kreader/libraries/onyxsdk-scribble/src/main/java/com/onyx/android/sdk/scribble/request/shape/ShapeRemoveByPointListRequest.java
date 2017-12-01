package com.onyx.android.sdk.scribble.request.shape;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPointList;
import com.onyx.android.sdk.scribble.request.BaseNoteRequest;
import com.onyx.android.sdk.scribble.shape.Shape;

import java.util.List;

/**
 * Created by zhuzeng on 7/3/16.
 */
public class ShapeRemoveByPointListRequest extends BaseNoteRequest {

    private volatile TouchPointList touchPointList;
    private volatile List<Shape> stash = null;
    private volatile SurfaceView surfaceView;

    public ShapeRemoveByPointListRequest(final TouchPointList list, final List<Shape> s, final SurfaceView view) {
        touchPointList = list;
        stash = s;
        surfaceView = view;
        setPauseInputProcessor(true);
        setRender(true);
    }

    public void execute(final NoteViewHelper helper) throws Exception {
        setResumeInputProcessor(helper.useDFBForCurrentState());
        benchmarkStart();
        if (stash != null) {
            helper.getNoteDocument().getCurrentPage(getContext()).addShapeList(stash);
        }
        helper.getNoteDocument().removeShapesByTouchPointList(getContext(), touchPointList, 1.0f);
        renderCurrentPage(helper);
        updateShapeDataInfo(helper);
        Log.e("############", "erase takes: " + benchmarkEnd());
        renderToScreen(helper);
    }

    private void renderToScreen(final NoteViewHelper helper) {
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
        Bitmap bitmap = helper.getRenderBitmap();
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

}
