package com.onyx.kreader.ui;

import android.content.Context;
import android.graphics.*;
import android.util.Log;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.BuildConfig;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.PageAnnotation;
import com.onyx.kreader.common.ReaderUserDataInfo;
import com.onyx.kreader.common.ReaderViewInfo;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.data.ReaderNoteDataInfo;
import com.onyx.kreader.ui.data.BookmarkIconFactory;
import com.onyx.kreader.ui.data.SingletonSharedPreference;
import com.onyx.kreader.ui.highlight.ReaderSelectionManager;
import com.onyx.kreader.utils.RectUtils;

import java.util.List;

/**
 * Created by joy on 7/25/16.
 */
public class ReaderPainter {

    private static final PixelXorXfermode xorMode = new PixelXorXfermode(Color.WHITE);
    private static final String TAG = ReaderPainter.class.getCanonicalName();
    private static boolean debugTestTouchPointCircle = false;
    private static boolean debugTestOffsetTouchPointCircle = false;
    private static boolean debugPageInfo = false;

    private enum DrawHighlightPaintStyle {UnderLine, Fill}

    public ReaderPainter() {
    }

    public void drawPage(Context context,
                         Canvas canvas,
                         final Bitmap bitmap,
                         final ReaderUserDataInfo userDataInfo,
                         final ReaderViewInfo viewInfo,
                         ReaderSelectionManager selectionManager,
                         NoteManager noteManager) {
        Paint paint = new Paint();
        drawBackground(canvas, paint);
        drawBitmap(canvas, paint, bitmap);
        drawViewportOverlayIndicator(canvas, paint, viewInfo);
        drawSearchResults(canvas, paint, userDataInfo, viewInfo, DrawHighlightPaintStyle.Fill);
        drawHighlightResult(canvas, paint, userDataInfo, viewInfo, selectionManager, DrawHighlightPaintStyle.Fill);
        drawAnnotations(context, canvas, paint, userDataInfo, viewInfo, DrawHighlightPaintStyle.Fill);
        drawBookmark(context, canvas, userDataInfo, viewInfo);
        drawShapes(context, canvas, paint, noteManager);
        drawStashShapes(context, canvas, paint, noteManager, viewInfo);
        drawShapeEraser(context, canvas, paint, noteManager);
        drawTestTouchPointCircle(context, canvas, paint, userDataInfo);
        drawPageInfo(canvas, paint, viewInfo);
    }

    private void drawBackground(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }

    private void drawBitmap(Canvas canvas, Paint paint, Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void drawPageInfo(final Canvas canvas, final Paint paint, final ReaderViewInfo viewInfo) {
        if (!(debugPageInfo && BuildConfig.DEBUG)) {
            return;
        }
        paint.setStrokeWidth(3.0f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        for(PageInfo pageInfo : viewInfo.getVisiblePages()) {
            canvas.drawRect(pageInfo.getDisplayRect(), paint);
        }
    }

    private void drawViewportOverlayIndicator(final Canvas canvas, final Paint paint, final ReaderViewInfo viewInfo) {
        if (viewInfo.getLastViewportOverlayPosition() != null) {
            paint.setColor(Color.GRAY);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);
            PathEffect effect = new DashPathEffect(new float[]{5,5,5,5},1);
            paint.setPathEffect(effect);
            canvas.drawLine(0, viewInfo.getLastViewportOverlayPosition().y,
                    viewInfo.viewportInDoc.width(), viewInfo.getLastViewportOverlayPosition().y,
                    paint);
            paint.setPathEffect(null);
        }
    }

    private void drawSearchResults(Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, DrawHighlightPaintStyle paintStyle) {
        drawReaderSelections(canvas, paint, viewInfo, userDataInfo.getSearchResults(), paintStyle);
    }

    private void drawHighlightResult(Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, ReaderSelectionManager selectionManager, DrawHighlightPaintStyle paintStyle) {
        if (userDataInfo.hasHighlightResult()) {
            drawReaderSelection(canvas, paint, viewInfo, userDataInfo.getHighlightResult(), paintStyle);
            drawSelectionCursor(canvas, paint, xorMode, selectionManager);
        }
    }

    private void drawAnnotations(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, DrawHighlightPaintStyle paintStyle) {
        if (!SingletonSharedPreference.isShowAnnotation(context)) {
            return;
        }
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            if (userDataInfo.hasPageAnnotations(pageInfo)) {
                List<PageAnnotation> annotations = userDataInfo.getPageAnnotations(pageInfo);
                for (PageAnnotation annotation : annotations) {
                    drawHighlightRectangles(canvas, paint, RectUtils.mergeRectanglesByBaseLine(annotation.getRectangles()), paintStyle);
                    String note = annotation.getAnnotation().getNote();
                    if (!StringUtils.isNullOrEmpty(note)){
                        drawHighLightSign(context, canvas, paint, annotation.getRectangles());
                    }
                }
            }
        }
    }

    private void drawBookmark(Context context, Canvas canvas, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        if (!SingletonSharedPreference.isShowBookmark(context)) {
            return;
        }
        Bitmap bitmap = BookmarkIconFactory.getBookmarkIcon(context, hasBookmark(userDataInfo, viewInfo));
        final Point point = BookmarkIconFactory.bookmarkPosition(canvas.getWidth(), bitmap);
        canvas.drawBitmap(bitmap, point.x, point.y, null);
    }

    private void drawReaderSelection(Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, ReaderSelection selection, DrawHighlightPaintStyle paintStyle) {
        PageInfo pageInfo = viewInfo.getPageInfo(selection.getPagePosition());
        if (pageInfo != null) {
            drawHighlightRectangles(canvas, paint, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()), paintStyle);
        }
    }

    private void drawReaderSelections(Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, List<ReaderSelection> list, DrawHighlightPaintStyle paintStyle) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (ReaderSelection sel : list) {
            drawReaderSelection(canvas, paint, viewInfo, sel, paintStyle);
        }
    }

    private void drawHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles, DrawHighlightPaintStyle paintStyle) {
        if (rectangles == null) {
            return;
        }
        switch (paintStyle){
            case UnderLine:
                drawUnderLineHighlightRectangles(canvas, paint, rectangles);
                break;
            case Fill:
                drawFillHighlightRectangles(canvas, paint, rectangles);
                break;
        }
    }

    private void drawUnderLineHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles){
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        for (int i = 0; i < rectangles.size(); ++i) {
            canvas.drawLine(rectangles.get(i).left, rectangles.get(i).bottom, rectangles.get(i).right, rectangles.get(i).bottom, paint);
        }
    }

    private void drawFillHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles){
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xorMode);
        for (int i = 0; i < rectangles.size(); ++i) {
            canvas.drawRect(rectangles.get(i), paint);
        }
    }

    private void drawHighLightSign(Context context, Canvas canvas, Paint paint, List<RectF> rectangles){
        if (rectangles == null || rectangles.size() < 1) {
            return;
        }
        RectF end = rectangles.get(rectangles.size() - 1);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_dialog_reader_choose_label_sign);
        canvas.drawBitmap(bitmap, end.right, end.top - bitmap.getHeight(), null);
    }

    private void drawSelectionCursor(Canvas canvas, Paint paint, PixelXorXfermode xor, ReaderSelectionManager selectionManager) {
        selectionManager.draw(canvas, paint, xor);
    }

    private void drawShapes(final Context context,
                            final Canvas canvas,
                            final Paint paint,
                            final NoteManager noteManager) {
        if (!SingletonSharedPreference.isShowNote(context)) {
            return;
        }
        final ReaderNoteDataInfo noteDataInfo = noteManager.getNoteDataInfo();
        if (noteDataInfo == null || !isShapeBitmapReady(noteManager, noteDataInfo) || !noteDataInfo.isContentRendered()) {
            return;
        }
        final Bitmap bitmap = noteManager.getViewBitmap();
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private void drawStashShapes(final Context context,
                            final Canvas canvas,
                            final Paint paint,
                            final NoteManager noteManager,
                            final ReaderViewInfo viewInfo) {
        if (!SingletonSharedPreference.isShowNote(context)) {
            return;
        }
        if (noteManager.isDFBForCurrentShape()) {
            return;
        }
        final PageInfo pageInfo = viewInfo.getFirstVisiblePage();
        final Matrix renderMatrix = new Matrix();
        RenderContext renderContext = RenderContext.create(canvas, paint, renderMatrix);
        renderMatrix.reset();
        renderMatrix.postScale(pageInfo.getActualScale(), pageInfo.getActualScale());
        renderMatrix.postTranslate(pageInfo.getDisplayRect().left, pageInfo.getDisplayRect().top);
        if (!CollectionUtils.isNullOrEmpty(noteManager.getShapeStash())) {
            for (Shape shape : noteManager.getShapeStash()) {
                shape.render(renderContext);
            }
        }
        if (noteManager.getCurrentShape() != null) {
            noteManager.getCurrentShape().render(renderContext);
        }
    }

    private void drawShapeEraser(final Context context,
                                 final Canvas canvas,
                                 final Paint paint,
                                 final NoteManager noteManager) {
        final TouchPoint touchPoint = noteManager.getNoteEventProcessorManager().getEraserPoint();
        if (touchPoint == null) {
            return;
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        canvas.drawCircle(touchPoint.x, touchPoint.y, noteManager.getNoteDrawingArgs().eraserRadius, paint);
    }

    private boolean hasBookmark(final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        return userDataInfo.hasBookmark(viewInfo.getFirstVisiblePage());
    }

    private boolean hasShapes(ReaderNoteDataInfo shapeDataInfo) {
        if (shapeDataInfo == null) {
            return false;
        }
        return shapeDataInfo.hasShapes();
    }

    private boolean isShapeBitmapReady(NoteManager noteManager, ReaderNoteDataInfo shapeDataInfo) {
        final Bitmap bitmap = noteManager.getViewBitmap();
        if (bitmap == null) {
            return false;
        }
        return true;
    }

    private void drawTestTouchPointCircle(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo){
        PointF touchPoint = userDataInfo.getTouchPoint();
        if (debugTestTouchPointCircle && touchPoint != null){
            canvas.drawCircle(touchPoint.x, touchPoint.y, 20, paint);
            if (debugTestOffsetTouchPointCircle){
                float offset = context.getResources().getDimension(R.dimen.move_point_offset_height);
                canvas.drawCircle(touchPoint.x, touchPoint.y - offset, 20, paint);
            }
        }
    }
}
