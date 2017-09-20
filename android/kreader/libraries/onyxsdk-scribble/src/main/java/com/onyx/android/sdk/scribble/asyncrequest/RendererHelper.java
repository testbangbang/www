package com.onyx.android.sdk.scribble.asyncrequest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.hanvon.core.Algorithm;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.scribble.R;
import com.onyx.android.sdk.scribble.data.LineLayoutArgs;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.SelectedRectF;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 12/8/2017.
 */

public class RendererHelper {

    private static final String TAG = "RendererHelper";

    private boolean useExternal = false;
    private boolean debugPathBenchmark = false;
    public DashPathEffect selectedDashPathEffect = new DashPathEffect(new float[]{4, 4, 4, 4}, 2);
    private ReaderBitmapImpl renderBitmapWrapper = new ReaderBitmapImpl();

    public void init() {
        renderBitmapWrapper.clear();
    }

    public Bitmap updateRenderBitmap(final Rect viewportSize) {
        renderBitmapWrapper.update(viewportSize.width(), viewportSize.height(), Bitmap.Config.ARGB_8888);
        return renderBitmapWrapper.getBitmap();
    }

    public Bitmap getRenderBitmap() {
        return renderBitmapWrapper.getBitmap();
    }

    public void clearRenderBitmap() {
        renderBitmapWrapper.clear();
    }

    private RenderContext createRenderContext(final NoteManager parent) {
        Bitmap bitmap = updateRenderBitmap(parent.getViewportSize());
        bitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = preparePaint(parent);
        final Matrix renderMatrix = new Matrix();
        return RenderContext.create(bitmap, canvas, paint, renderMatrix);
    }

    private void renderNormalVisiblePages(final NoteManager parent, final AsyncBaseNoteRequest request) {
        final RenderContext renderContext = createRenderContext(parent);
        drawNormalLayoutBackground(parent, renderContext.canvas, renderContext.paint);

        prepareRenderingBuffer(renderContext.bitmap);
        renderVisiblePages(parent, request, renderContext);

        flushRenderingBuffer(renderContext.bitmap);
        drawRandomTestPath(request, renderContext.canvas, renderContext.paint);
    }

    private void renderSpanVisiblePages(final NoteManager parent, final AsyncBaseNoteRequest request) {
        final RenderContext renderContext = createRenderContext(parent);

        prepareRenderingBuffer(renderContext.bitmap);
        renderVisiblePages(parent, request, renderContext);

        renderSpanCursorShape(parent, renderContext);
        drawSpanLayoutBackground(parent, renderContext);

        flushRenderingBuffer(renderContext.bitmap);
        drawRandomTestPath(request, renderContext.canvas, renderContext.paint);
    }

    private void renderVisiblePages(final NoteManager parent,
                                    final AsyncBaseNoteRequest request,
                                    final RenderContext renderContext) {
        for (PageInfo page : request.getVisiblePages()) {
            final NotePage notePage = parent.getNoteDocument().getNotePage(parent.getAppContext(), page.getName());
            SelectedRectF selectedRectF = notePage.getSelectedRect();
            renderContext.force = selectedRectF != null;
            notePage.render(renderContext, null);
            renderSelectedRect(selectedRectF.getRectF(), renderContext, selectedRectF.getOrientation());
        }
    }

    public void renderVisiblePagesInBitmap(final NoteManager parent, final AsyncBaseNoteRequest request) {
        if (parent.inSpanLayoutMode()) {
            renderSpanVisiblePages(parent, request);
        }else {
            renderNormalVisiblePages(parent, request);
        }
    }

    private void renderSpanCursorShape(final NoteManager parent, final RenderContext renderContext) {
        Shape cursorShape = parent.getSpanCursorShape();
        if (cursorShape == null) {
            return;
        }
        cursorShape.render(renderContext);
    }

    private void drawSpanLayoutBackground(final NoteManager parent, final RenderContext renderContext) {
        if (parent.getHostView() == null) {
            return;
        }
        if (parent.getDocumentHelper().getLineLayoutBackground() == NoteBackgroundType.EMPTY) {
            return;
        }
        LineLayoutArgs args = parent.getLineLayoutArgs();
        if (args == null) {
            return;
        }

        Rect viewRect = new Rect();
        parent.getHostView().getLocalVisibleRect(viewRect);
        int count = args.getLineCount();
        int lineHeight = args.getLineHeight();
        int baseline = args.getBaseLine();
        Paint paint = new Paint();
        for (int i = 0; i < count; i++) {
            renderContext.canvas.drawLine(viewRect.left, baseline + 1, viewRect.right, baseline + 1, paint);
            baseline += lineHeight;
        }
    }

    private void drawNormalLayoutBackground(final NoteManager parent, final Canvas canvas, final Paint paint) {
        drawBackground(parent.getAppContext(),
                canvas, paint, parent.getNoteDocument().getBackground(),
                parent.getNoteDocument().getNoteDrawingArgs().bgFilePath);
    }

    public void renderSelectionRectangle(final NoteManager parent, final TouchPoint start, final TouchPoint end) {
        Bitmap bitmap = updateRenderBitmap(parent.getViewportSize());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = preparePaint(parent);
        //TODO:when using dash effect,disable antialias for drawing rect.enhanced drawing speed.
        paint.setAntiAlias(false);
        paint.setPathEffect(selectedDashPathEffect);
        RectF rect = new RectF(start.getX(), start.getY(), end.getX(), end.getY());
        canvas.drawRect(rect, paint);
    }

    public void renderSelectedRect(RectF selectedRectF, RenderContext renderContext, float orientation) {
        if (selectedRectF == null || selectedRectF.width() < 0 || selectedRectF.height() < 0) {
            return;
        }
        Paint boundingPaint = new Paint(Color.BLACK);
        boundingPaint.setStyle(Paint.Style.STROKE);
        boundingPaint.setPathEffect(selectedDashPathEffect);
        if (orientation != 0) {
            renderContext.canvas.save();
            renderContext.canvas.rotate(orientation, selectedRectF.centerX(), selectedRectF.centerY());
        }
        renderContext.canvas.drawRect(selectedRectF, boundingPaint);
        if (orientation != 0) {
            renderContext.canvas.restore();
        }
        drawCornerPoint(selectedRectF, renderContext, orientation);
        drawRotationHandler(selectedRectF, renderContext, orientation);
    }

    private void drawCornerPoint(RectF selectedRectF, RenderContext renderContext, float orientation) {
        Paint cornerPointPaint = new Paint(Color.BLACK);
        cornerPointPaint.setStyle(Paint.Style.FILL);
        ArrayList<PointF> dragPointList = new ArrayList<>();
        dragPointList.add(new PointF(selectedRectF.left, selectedRectF.top));
        dragPointList.add(new PointF(selectedRectF.right, selectedRectF.top));
        dragPointList.add(new PointF(selectedRectF.left, selectedRectF.bottom));
        dragPointList.add(new PointF(selectedRectF.right, selectedRectF.bottom));
        if (orientation != 0) {
            renderContext.canvas.save();
            renderContext.canvas.rotate(orientation, selectedRectF.centerX(), selectedRectF.centerY());
        }
        for (PointF pointF : dragPointList) {
            renderContext.canvas.drawCircle(pointF.x, pointF.y, 5, cornerPointPaint);
        }
        if (orientation != 0) {
            renderContext.canvas.restore();
        }
    }

    private void drawRotationHandler(RectF selectedRectF, RenderContext renderContext, float orientation) {
        Paint rotationHandlerPaint = new Paint(Color.BLACK);
        rotationHandlerPaint.setStyle(Paint.Style.STROKE);
        float rotationPointYCoordinate = Float.compare(selectedRectF.top - 50, 0) < 0 ? 0f : selectedRectF.top - 50;
        if (orientation != 0) {
            renderContext.canvas.save();
            renderContext.canvas.rotate(orientation, selectedRectF.centerX(), selectedRectF.centerY());
        }
        renderContext.canvas.drawCircle(selectedRectF.centerX(), rotationPointYCoordinate, 10, rotationHandlerPaint);
        rotationHandlerPaint.setStyle(Paint.Style.FILL);
        rotationHandlerPaint.setPathEffect(selectedDashPathEffect);
        renderContext.canvas.drawLine(selectedRectF.centerX(), selectedRectF.top,
                selectedRectF.centerX(), rotationPointYCoordinate, rotationHandlerPaint);
        if (orientation != 0) {
            renderContext.canvas.restore();
        }
    }

    private Paint preparePaint(final NoteManager parent) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(parent.getNoteDocument().getStrokeWidth());
        return paint;
    }

    private void prepareRenderingBuffer(final Bitmap bitmap) {
        if (!useExternal) {
            return;
        }
        Algorithm.initializeEx(bitmap.getWidth(), bitmap.getHeight(), bitmap);
    }

    private void flushRenderingBuffer(final Bitmap bitmap) {
    }

    private void drawBackground(final Context context,
                                final Canvas canvas,
                                final Paint paint,
                                int bgType,
                                String bgFilePath) {
        int bgResID = 0;
        switch (bgType) {
            case NoteBackgroundType.EMPTY:
                return;
            case NoteBackgroundType.LINE:
                bgResID = R.drawable.scribble_back_ground_line;
                break;
            case NoteBackgroundType.GRID:
                bgResID = R.drawable.scribble_back_ground_grid;
                break;
            case NoteBackgroundType.MUSIC:
                bgResID = R.drawable.scribble_back_ground_music;
                break;
            case NoteBackgroundType.ENGLISH:
                bgResID = R.drawable.scribble_back_ground_english;
                break;
            case NoteBackgroundType.MATS:
                bgResID = R.drawable.scribble_back_ground_mats;
                break;
            case NoteBackgroundType.TABLE:
                bgResID = R.drawable.scribble_back_ground_table_grid;
                break;
            case NoteBackgroundType.COLUMN:
                bgResID = R.drawable.scribble_back_ground_line_column;
                break;
            case NoteBackgroundType.LEFT_GRID:
                bgResID = R.drawable.scribble_back_ground_left_grid;
                break;
            case NoteBackgroundType.GRID_5_5:
                bgResID = R.drawable.scribble_back_ground_grid_5_5;
                break;
            case NoteBackgroundType.GRID_POINT:
                bgResID = R.drawable.scribble_back_ground_grid_point;
                break;
            case NoteBackgroundType.LINE_1_6:
                bgResID = R.drawable.scribble_back_ground_line_1_6;
                break;
            case NoteBackgroundType.LINE_2_0:
                bgResID = R.drawable.scribble_back_ground_line_2_0;
                break;
            case NoteBackgroundType.CALENDAR:
                bgResID = R.drawable.scribble_back_ground_calendar;
                break;
            case NoteBackgroundType.FILE:
                bgResID = Integer.MIN_VALUE;
                break;
        }
        drawBackgroundResource(context, canvas, paint, bgResID, bgFilePath);

    }

    private void drawBackgroundResource(final Context context,
                                        Canvas canvas,
                                        Paint paint,
                                        int resID,
                                        String bgFilePath) {
        Bitmap bitmap;
        Rect dest;
        if (resID == Integer.MIN_VALUE && !TextUtils.isEmpty(bgFilePath)) {
            bitmap = BitmapFactory.decodeFile(bgFilePath);
            if (bitmap.getHeight() < bitmap.getWidth()) {
                bitmap = BitmapUtils.rotateBmp(bitmap, 90);
            }
            dest = BitmapUtils.getScaleInSideAndCenterRect(
                    canvas.getHeight(), canvas.getWidth(), bitmap.getHeight(), bitmap.getWidth(), false);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), resID);
            dest = new Rect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
        }
        Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        if (DeviceConfig.isColorDevice() && resID != Integer.MIN_VALUE) {
            canvas.drawBitmap(bitmap, 0, 0, paint);
        } else {
            canvas.drawBitmap(bitmap, src, dest, paint);
        }
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private boolean isRenderRandomTestPath() {
        return debugPathBenchmark;
    }

    private void drawRandomTestPath(final AsyncBaseNoteRequest request,
                                    final Canvas canvas,
                                    final Paint paint) {
        if (!isRenderRandomTestPath()) {
            return;
        }
        Path path = new Path();
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int max = TestUtils.randInt(0, 1000);
        path.moveTo(TestUtils.randInt(0, width), TestUtils.randInt(0, height));
        for(int i = 0; i < max; ++i) {
            float xx = TestUtils.randInt(0, width);
            float yy = TestUtils.randInt(0, height);
            float xx2 = TestUtils.randInt(0, width);
            float yy2 = TestUtils.randInt(0, height);
            path.quadTo((xx + xx2) / 2, (yy + yy2) / 2, xx2, yy2);
            if (request.isAbort()) {
                return;
            }
        }
        long ts = System.currentTimeMillis();
        canvas.drawPath(path, paint);
    }

    public void currentPageAsVisiblePage(final NoteManager noteManager, final AsyncBaseNoteRequest request) {
        final NotePage notePage = noteManager.getNoteDocument().getCurrentPage(request.getContext());
        request.getVisiblePages().clear();
        PageInfo pageInfo = new PageInfo(notePage.getPageUniqueId(),
                noteManager.getViewportSize().width(),
                noteManager.getViewportSize().height());
        pageInfo.updateDisplayRect(noteManager.getViewportSizeF());
        request.getVisiblePages().add(pageInfo);
    }

    public void renderCurrentPageInBitmap(final NoteManager noteManager, final AsyncBaseNoteRequest request) {
        if (!request.isRenderToBitmap()) {
            return;
        }
        currentPageAsVisiblePage(noteManager, request);
        renderVisiblePagesInBitmap(noteManager, request);
    }

    public void renderToSurfaceView(List<Shape> shapes, SurfaceView surfaceView) {
        if (shapes == null) {
            return;
        }
        Rect rect = checkSurfaceView(surfaceView);
        if (rect == null) {
            return;
        }

        applyUpdateMode(surfaceView);
        Canvas canvas = surfaceView.getHolder().lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        Paint paint = new Paint();
        clearBackground(canvas, paint, rect);
        canvas.drawBitmap(getRenderBitmap(), 0, 0, paint);
        RenderContext renderContext = RenderContext.create(canvas, paint, null);
        for (Shape shape : shapes) {
            shape.render(renderContext);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private Rect checkSurfaceView(SurfaceView surfaceView) {
        if (surfaceView == null || !surfaceView.getHolder().getSurface().isValid()) {
            Log.e(TAG, "surfaceView is not valid");
            return null;
        }
        return new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight());
    }


    private void clearBackground(final Canvas canvas, final Paint paint, final Rect rect) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);
    }

    private void applyUpdateMode(View view) {
        if (false) {
            EpdController.setViewDefaultUpdateMode(view, UpdateMode.GC);
        } else {
            EpdController.resetUpdateMode(view);
        }
    }

    public void drawErasingIndicator(final SurfaceView view, final Paint paint, final TouchPoint erasePoint, float drawRadius) {
        if (erasePoint == null || erasePoint.getX() <= 0 || erasePoint.getY() <= 0) {
            return;
        }

        float x = erasePoint.getX();
        float y = erasePoint.getY();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        Canvas canvas = view.getHolder().lockCanvas();
        canvas.drawCircle(x, y, drawRadius, paint);
        view.getHolder().unlockCanvasAndPost(canvas);
    }
}
