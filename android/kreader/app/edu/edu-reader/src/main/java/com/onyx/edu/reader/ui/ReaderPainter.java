package com.onyx.edu.reader.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PixelXorXfermode;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.ui.compat.AppCompatUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.edu.reader.BuildConfig;
import com.onyx.edu.reader.R;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.reader.common.ReaderUserDataInfo;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.edu.reader.note.NoteManager;
import com.onyx.edu.reader.note.data.ReaderNoteDataInfo;
import com.onyx.edu.reader.ui.data.BookmarkIconFactory;
import com.onyx.edu.reader.ui.data.SingletonSharedPreference;
import com.onyx.edu.reader.ui.data.SingletonSharedPreference.AnnotationHighlightStyle;
import com.onyx.edu.reader.ui.highlight.ReaderSelectionManager;
import com.onyx.android.sdk.utils.RectUtils;

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
    private AnnotationHighlightStyle annotationHighlightStyle;

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
        drawCropRectIndicator(canvas, paint, viewInfo);
        drawViewportOverlayIndicator(canvas, paint, viewInfo);
        drawBookmark(context, canvas, userDataInfo, viewInfo);
        drawSearchResults(context, canvas, paint, userDataInfo, viewInfo, annotationHighlightStyle);
        drawHighlightResult(context, canvas, paint, userDataInfo, viewInfo, selectionManager, annotationHighlightStyle);
        drawAnnotations(context, canvas, paint, userDataInfo, viewInfo, annotationHighlightStyle);
        drawPageLinks(context, canvas, paint, userDataInfo, viewInfo);
        drawShapeContents(context, canvas, paint, userDataInfo, viewInfo, noteManager);
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
        paint.setDither(true);
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

    private void initPaintWithAuxiliaryLineStyle(final Paint paint) {
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        PathEffect effect = new DashPathEffect(new float[]{5,5,5,5},1);
        paint.setPathEffect(effect);
    }

    private void resetPaintFromAuxiliaryLineStyle(final Paint paint) {
        paint.setPathEffect(null);
    }

    private boolean skipCropRectIndicator(final ReaderViewInfo viewInfo) {
        return viewInfo.subScreenCount <= 1;
    }

    private void drawCropRectIndicator(final Canvas canvas, final Paint paint, final ReaderViewInfo viewInfo) {
        if (viewInfo.cropRegionInViewport == null ||
                viewInfo.cropRegionInViewport.isEmpty() ||
                skipCropRectIndicator(viewInfo)) {
            return;
        }
        initPaintWithAuxiliaryLineStyle(paint);
        canvas.drawRect(viewInfo.cropRegionInViewport, paint);
        resetPaintFromAuxiliaryLineStyle(paint);
    }

    private void drawViewportOverlayIndicator(final Canvas canvas, final Paint paint, final ReaderViewInfo viewInfo) {
        if (viewInfo.getLastViewportOverlayPosition() != null) {
            initPaintWithAuxiliaryLineStyle(paint);
            canvas.drawLine(0, viewInfo.getLastViewportOverlayPosition().y,
                    viewInfo.viewportInDoc.width(), viewInfo.getLastViewportOverlayPosition().y,
                    paint);
            resetPaintFromAuxiliaryLineStyle(paint);
        }
    }

    private void drawSearchResults(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, AnnotationHighlightStyle highlightStyle) {
        drawReaderSelections(context, canvas, paint, viewInfo, userDataInfo.getSearchResults(), highlightStyle);
    }

    private void drawHighlightResult(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, ReaderSelectionManager selectionManager, AnnotationHighlightStyle highlightStyle) {
        if (userDataInfo.hasHighlightResult()) {
            drawReaderSelection(context, canvas, paint, viewInfo, userDataInfo.getHighlightResult(), highlightStyle);
            drawSelectionCursor(canvas, paint, xorMode, selectionManager);
        }
    }

    private void drawAnnotations(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, AnnotationHighlightStyle highlightStyle) {
        if (!SingletonSharedPreference.isShowAnnotation(context)) {
            return;
        }
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            if (userDataInfo.hasPageAnnotations(pageInfo)) {
                List<PageAnnotation> annotations = userDataInfo.getPageAnnotations(pageInfo);
                for (PageAnnotation annotation : annotations) {
                    drawHighlightRectangles(context, canvas, RectUtils.mergeRectanglesByBaseLine(annotation.getRectangles()), highlightStyle);
                    String note = annotation.getAnnotation().getNote();
                    if (!StringUtils.isNullOrEmpty(note)){
                        drawHighLightSign(context, canvas, paint, annotation.getRectangles());
                    }
                }
            }
        }
    }

    private void drawPageLinks(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            if (!userDataInfo.hasPageLinks(pageInfo)) {
                continue;
            }
            List<ReaderSelection> links = userDataInfo.getPageLinks(pageInfo);
            for (ReaderSelection link : links) {
                drawUnderLineHighlightRectangles(canvas, paint, link.getRectangles());
            }
        }
    }

    private void drawShapeContents(Context context,
                                   Canvas canvas,
                                   Paint paint,
                                   final ReaderUserDataInfo userDataInfo,
                                   final ReaderViewInfo viewInfo,
                                   final NoteManager noteManager) {
        if (!viewInfo.supportScalable) {
            return;
        }
        drawShapes(context, canvas, paint, userDataInfo, noteManager);
        drawStashShapes(context, canvas, paint, noteManager, viewInfo);
        drawShapeEraser(context, canvas, paint, noteManager);
    }

    private void drawBookmark(Context context, Canvas canvas, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        if (!SingletonSharedPreference.isShowBookmark(context)) {
            return;
        }
        if (hasFormField(userDataInfo, viewInfo)) {
            return;
        }
        Bitmap bitmap = BookmarkIconFactory.getBookmarkIcon(context, hasBookmark(userDataInfo, viewInfo));
        final Point point = BookmarkIconFactory.bookmarkPosition(canvas.getWidth(), bitmap);
        float left = AppCompatUtils.calculateEvenDigital(point.x);
        float top = AppCompatUtils.calculateEvenDigital(point.y);
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private void drawReaderSelection(Context context, Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, ReaderSelection selection, AnnotationHighlightStyle highlightStyle) {
        PageInfo pageInfo = viewInfo.getPageInfo(selection.getPagePosition());
        if (pageInfo != null) {
            drawHighlightRectangles(context, canvas, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()), highlightStyle);
        }
    }

    private void drawReaderSelections(Context context, Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, List<ReaderSelection> list, AnnotationHighlightStyle highlightStyle) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (ReaderSelection sel : list) {
            drawReaderSelection(context, canvas, paint, viewInfo, sel, highlightStyle);
        }
    }

    private void drawHighlightRectangles(Context context, Canvas canvas, List<RectF> rectangles, AnnotationHighlightStyle highlightStyle) {
        if (rectangles == null) {
            return;
        }
        Paint paint = new Paint();
        switch (highlightStyle){
            case Underline:
                drawUnderLineHighlightRectangles(canvas, paint, rectangles);
                break;
            case Highlight:
                drawFillHighlightRectangles(canvas, paint, rectangles);
                break;
        }
    }

    private void drawUnderLineHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles){
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        int size = rectangles.size();
        for (int i = 0; i < size; ++i) {
            canvas.drawLine(rectangles.get(i).left, rectangles.get(i).bottom, rectangles.get(i).right, rectangles.get(i).bottom, paint);
        }
    }

    private void drawFillHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles){
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xorMode);
        int size = rectangles.size();
        for (int i = 0; i < size; ++i) {
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
                            final ReaderUserDataInfo userDataInfo,
                            final NoteManager noteManager) {
        boolean showNote = SingletonSharedPreference.isShowNote(context);
        if (!showNote || userDataInfo.hasHighlightResult()) {
            return;
        }
        final ReaderNoteDataInfo noteDataInfo = noteManager.getNoteDataInfo();
        if (!noteDataInfo.isRequestFinished() || !isShapeBitmapReady(noteManager, noteDataInfo) || !noteDataInfo.isContentRendered()) {
            return;
        }
        final Bitmap bitmap = noteManager.getViewBitmap();
        canvas.drawBitmap(bitmap, 0, 0, paint);

        final Bitmap review = noteManager.getReviewBitmap();
        canvas.drawBitmap(review, 0, 0, paint);
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
        renderMatrix.reset();
        renderMatrix.postScale(pageInfo.getActualScale(), pageInfo.getActualScale());
        renderMatrix.postTranslate(pageInfo.getDisplayRect().left, pageInfo.getDisplayRect().top);
        RenderContext renderContext = RenderContext.create(canvas, paint, renderMatrix);
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
        final TouchPoint touchPoint = noteManager.getEraserPoint();
        if (touchPoint == null) {
            return;
        }
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.0f);
        canvas.drawCircle(touchPoint.x, touchPoint.y, noteManager.getNoteDataInfo().getEraserRadius(), paint);
    }

    private boolean hasBookmark(final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            if (userDataInfo.hasBookmark(pageInfo)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasFormField(final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            if (userDataInfo.hasFormFields(pageInfo)) {
                return true;
            }
        }
        return false;
    }

    private boolean isShapeBitmapReady(NoteManager noteManager, ReaderNoteDataInfo shapeDataInfo) {
        final Bitmap bitmap = noteManager.getViewBitmap();
        if (bitmap == null) {
            return false;
        }
        return true;
    }

    private void drawTestTouchPointCircle(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        PointF touchPoint = userDataInfo.getTouchPoint();
        if (debugTestTouchPointCircle && touchPoint != null){
            canvas.drawCircle(touchPoint.x, touchPoint.y, 20, paint);
            if (debugTestOffsetTouchPointCircle){
                float offset = context.getResources().getDimension(R.dimen.move_point_offset_height);
                canvas.drawCircle(touchPoint.x, touchPoint.y - offset, 20, paint);
            }
        }
    }

    public void setAnnotationHighlightStyle(AnnotationHighlightStyle annotationHighlightStyle) {
        this.annotationHighlightStyle = annotationHighlightStyle;
    }
}
