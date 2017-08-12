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
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.R;
import com.onyx.android.sdk.scribble.data.NoteBackgroundType;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.utils.DeviceConfig;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.List;

/**
 * Created by john on 12/8/2017.
 */

public class RenderHelper {

    private boolean useExternal = false;
    private boolean debugPathBenchmark = false;

    public void renderVisiblePagesInBitmap(final NoteManager parent, final List<PageInfo> visiblePages) {
        Bitmap bitmap = parent.updateRenderBitmap(parent.getViewportSize());
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
        for (PageInfo page : visiblePages) {
            final NotePage notePage = parent.getNoteDocument().getNotePage(context, page.getName());
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

    public void renderSelectionRectangle(final NoteManager parent, final TouchPoint start, final TouchPoint end) {
        Bitmap bitmap = parent.updateRenderBitmap(parent.getViewportSize());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = preparePaint(parent);
        paint.setPathEffect(parent.selectedDashPathEffect);
        RectF rect = new RectF(start.getX(), start.getY(), end.getX(), end.getY());
        canvas.drawRect(rect, paint);
    }

    public void renderShapeMovingRectangle(final NoteManager parent, final TouchPoint start, final TouchPoint end) {
        Bitmap bitmap = parent.updateRenderBitmap(parent.getViewportSize());
        Canvas canvas = new Canvas(bitmap);
        Paint paint = preparePaint(parent);
        paint.setPathEffect(parent.selectedDashPathEffect);
        RectF rect = parent.getNoteDocument().getCurrentPage(getContext()).getSelectedRect();
        rect.offset(end.getX() - rect.centerX(), end.getY() - rect.centerY());
        canvas.drawRect(rect, paint);
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

    public void renderCurrentPageInBitmap(final NoteManager helper) {
        if (!isRenderToBitmap()) {
            return;
        }
        currentPageAsVisiblePage(helper);
        renderVisiblePagesInBitmap(helper);
    }
}
