package com.onyx.android.sdk.scribble.asyncrequest;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import com.hanvon.core.Algorithm;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.common.request.RequestManager;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.R;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NoteDrawingArgs;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuzeng on 6/3/16.
 * Requests are used in standalone application or separate page rendering from
 * shape rendering.
 */
public class AsyncBaseNoteRequest extends BaseRequest {

    private volatile ShapeDataInfo shapeDataInfo;
    private String docUniqueId;
    private String parentLibraryId;
    private Rect viewportSize;
    private List<PageInfo> visiblePages = new ArrayList<>();
    private boolean debugPathBenchmark = false;
    private boolean pauseInputProcessor = true;
    private boolean resumeInputProcessor = false;
    private volatile boolean renderToBitmap = true;
    private volatile boolean renderToScreen = true;
    private boolean useExternal = false;
    private String identifier;

    public boolean isResumeInputProcessor() {
        return resumeInputProcessor;
    }

    public void setResumeInputProcessor(boolean resumeInputProcessor) {
        this.resumeInputProcessor = resumeInputProcessor;
    }

    public boolean isPauseInputProcessor() {
        return pauseInputProcessor;
    }

    public void setPauseInputProcessor(boolean pauseInputProcessor) {
        this.pauseInputProcessor = pauseInputProcessor;
    }

    public boolean isRenderToBitmap() {
        return renderToBitmap;
    }

    public void setRenderToBitmap(boolean render) {
        renderToBitmap = render;
    }

    public boolean isRenderToScreen() {
        return renderToScreen;
    }

    public void setRenderToScreen(boolean render) {
        renderToScreen = render;
    }

    public void setRender(boolean render) {
        setRenderToBitmap(render);
        setRenderToScreen(render);
    }

    public AsyncBaseNoteRequest() {
        setAbortPendingTasks(true);
    }

    public void setDocUniqueId(final String id) {
        docUniqueId = id;
    }

    public final String getDocUniqueId() {
        return docUniqueId;
    }

    public String getParentLibraryId() {
        return parentLibraryId;
    }

    public void setParentLibraryId(String parentLibraryId) {
        this.parentLibraryId = parentLibraryId;
    }

    public void setViewportSize(final Rect size) {
        viewportSize = size;
    }

    public final Rect getViewportSize() {
        return viewportSize;
    }

    public void setVisiblePages(final List<PageInfo> pages) {
        visiblePages.addAll(pages);
    }

    public final List<PageInfo> getVisiblePages() {
        return visiblePages;
    }

    public void beforeExecute(final AsyncNoteViewHelper helper) {
        helper.getRequestManager().acquireWakeLock(getContext(), getClass().getSimpleName());
        if (isPauseInputProcessor()) {
            helper.pauseDrawing();
        }
        benchmarkStart();
        invokeStartCallback(helper.getRequestManager());
    }

    private void invokeStartCallback(final RequestManager requestManager) {
        if (getCallback() == null) {
            return;
        }
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getCallback().start(AsyncBaseNoteRequest.this);
            }
        };
        if (isRunInBackground()) {
            requestManager.getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public void execute(final AsyncNoteViewHelper helper) throws Exception {
    }

    /**
     * drawToView Instantly when finish request,reused isRender Flag
     * @param helper
     */
    public void postExecute(final AsyncNoteViewHelper helper){
        if (getException() != null) {
            getException().printStackTrace();
        }

        if (isRenderToScreen()) {
            helper.enableScreenPost(true);
            helper.renderToSurfaceView();
        }
        benchmarkEnd();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (getCallback() != null) {
                    getCallback().done(AsyncBaseNoteRequest.this, getException());
                }
                if (isResumeInputProcessor()) {
                    helper.resumeDrawing();
                }
                helper.getRequestManager().releaseWakeLock();
            }};

        if (isRunInBackground()) {
            helper.getRequestManager().getLooperHandler().post(runnable);
        } else {
            runnable.run();
        }
    }

    public final ShapeDataInfo getShapeDataInfo() {
        if (shapeDataInfo == null) {
            shapeDataInfo = new ShapeDataInfo();
        }
        return shapeDataInfo;
    }

    public void renderVisiblePagesInBitmap(final AsyncNoteViewHelper parent) {
        Bitmap bitmap = parent.updateRenderBitmap(getViewportSize());
        bitmap.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = preparePaint(parent);

        if (!parent.isLineLayoutMode()) {
            drawBackground(canvas, paint, parent.getNoteDocument().getBackground(),
                    parent.getNoteDocument().getNoteDrawingArgs().bgFilePath);
        }
        prepareRenderingBuffer(bitmap);

        final Matrix renderMatrix = new Matrix();
        final RenderContext renderContext = RenderContext.create(bitmap, canvas, paint, renderMatrix);
        for (PageInfo page : getVisiblePages()) {
            final NotePage notePage = parent.getNoteDocument().getNotePage(getContext(), page.getName());
            //TODO:if select Rect is not null,means some shape is selected.with shape transform,we always need to reConfig point by matrix.
            renderContext.force = notePage.getSelectedRect() != null;
            notePage.render(renderContext, null);
            parent.renderSelectedRect(notePage.getSelectedRect(), renderContext);
        }
        parent.renderCursorShape(renderContext);
        parent.drawLineLayoutBackground(renderContext);

        flushRenderingBuffer(bitmap);
        drawRandomTestPath(canvas, paint);
    }

    public void renderSelectionRectangle(final AsyncNoteViewHelper parent, final TouchPoint start, final TouchPoint end) {
        Bitmap bitmap = parent.updateRenderBitmap(getViewportSize());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = preparePaint(parent);
        paint.setPathEffect(parent.selectedDashPathEffect);
        RectF rect = new RectF(start.getX(), start.getY(), end.getX(), end.getY());
        canvas.drawRect(rect, paint);
    }

    public void renderShapeMovingRectangle(final AsyncNoteViewHelper parent, final TouchPoint start, final TouchPoint end) {
        Bitmap bitmap = parent.updateRenderBitmap(getViewportSize());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = preparePaint(parent);
        paint.setPathEffect(parent.selectedDashPathEffect);
        RectF rect = parent.getNoteDocument().getCurrentPage(getContext()).getSelectedRect();
        rect.offset(end.getX() - rect.centerX(), end.getY() - rect.centerY());
        canvas.drawRect(rect, paint);
    }

    private Paint preparePaint(final AsyncNoteViewHelper parent) {
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

    private void drawBackground(final Canvas canvas, final Paint paint, int bgType, String bgFilePath) {
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
        drawBackgroundResource(canvas, paint, bgResID, bgFilePath);

    }

    private void drawBackgroundResource(Canvas canvas, Paint paint, int resID, String bgFilePath) {
        Bitmap bitmap;
        Rect dest;
        if (resID == Integer.MIN_VALUE && !TextUtils.isEmpty(bgFilePath)) {
            bitmap = BitmapFactory.decodeFile(bgFilePath);
            //TODO:use dynamic rotation angle?(if does,need more info from screenshot).
            if (bitmap.getHeight() < bitmap.getWidth()) {
                bitmap = BitmapUtils.rotateBmp(bitmap, 90);
            }
            dest = BitmapUtils.getScaleInSideAndCenterRect(
                    canvas.getHeight(), canvas.getWidth(), bitmap.getHeight(), bitmap.getWidth(), false);
        } else {
            bitmap = BitmapFactory.decodeResource(getContext().getResources(), resID);
            dest = new Rect(0, 0, canvas.getWidth() - 1, canvas.getHeight() - 1);
        }
        Rect src = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
        if (DeviceConfig.isColorDevice()) {
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

    private void drawRandomTestPath(final Canvas canvas, final Paint paint) {
        if (!isRenderRandomTestPath()) {
            return;
        }
        Path path = new Path();
        int width = getViewportSize().width();
        int height = getViewportSize().height();
        int max = TestUtils.randInt(0, 1000);
        path.moveTo(TestUtils.randInt(0, width), TestUtils.randInt(0, height));
        for(int i = 0; i < max; ++i) {
            float xx = TestUtils.randInt(0, width);
            float yy = TestUtils.randInt(0, height);
            float xx2 = TestUtils.randInt(0, width);
            float yy2 = TestUtils.randInt(0, height);
            path.quadTo((xx + xx2) / 2, (yy + yy2) / 2, xx2, yy2);
            if (isAbort()) {
                return;
            }
        }
        long ts = System.currentTimeMillis();
        canvas.drawPath(path, paint);
    }

    public void currentPageAsVisiblePage(final AsyncNoteViewHelper helper) {
        final NotePage notePage = helper.getNoteDocument().getCurrentPage(getContext());
        getVisiblePages().clear();
        PageInfo pageInfo = new PageInfo(notePage.getPageUniqueId(), getViewportSize().width(), getViewportSize().height());
        pageInfo.updateDisplayRect(new RectF(0, 0, getViewportSize().width(), getViewportSize().height()));
        getVisiblePages().add(pageInfo);
    }

    public void renderCurrentPageInBitmap(final AsyncNoteViewHelper helper) {
        if (!isRenderToBitmap()) {
            return;
        }
        currentPageAsVisiblePage(helper);
        renderVisiblePagesInBitmap(helper);
    }


    public void updateShapeDataInfo(final AsyncNoteViewHelper parent) {
        final ShapeDataInfo shapeDataInfo = getShapeDataInfo();
        parent.updateShapeDataInfo(getContext(), shapeDataInfo);
    }

    public void ensureDocumentOpened(final AsyncNoteViewHelper parent) {
        if (!parent.getNoteDocument().isOpen()) {
            parent.getNoteDocument().open(getContext(),
                    getDocUniqueId(),
                    getParentLibraryId());
        }
    }

    public void syncDrawingArgs(final NoteDrawingArgs args) {
        getShapeDataInfo().getDrawingArgs().copyFrom(args);
    }

    public final NoteDrawingArgs getDrawingArgs() {
        return getShapeDataInfo().getDrawingArgs();
    }

    public void setIdentifier(final String id) {
        identifier = id;
    }

    public String getIdentifier() {
        return identifier;
    }
}
