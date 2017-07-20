package com.onyx.android.dr.reader.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.PixelXorXfermode;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;


import com.onyx.android.dr.BuildConfig;
import com.onyx.android.dr.R;
import com.onyx.android.dr.reader.data.BookmarkIconFactory;
import com.onyx.android.dr.reader.data.SingletonSharedPreference;
import com.onyx.android.dr.reader.highlight.ReaderSelectionManager;
import com.onyx.android.dr.reader.utils.AppCompatUtils;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.reader.common.ReaderUserDataInfo;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.List;

/**
 * Created by huxiaomao on 17/5/4.
 */

public class ReaderPaint {
    private static final PixelXorXfermode xorMode = new PixelXorXfermode(Color.WHITE);
    private static final String TAG = ReaderPaint.class.getCanonicalName();
    private static boolean debugTestTouchPointCircle = false;
    private static boolean debugTestOffsetTouchPointCircle = false;
    private static boolean debugPageInfo = false;
    private SingletonSharedPreference.AnnotationHighlightStyle annotationHighlightStyle;

    public ReaderPaint() {

    }

    public void drawPage(Context context,
                         Canvas canvas,
                         final Bitmap bitmap,
                         final ReaderUserDataInfo userDataInfo,
                         final ReaderViewInfo viewInfo,
                         ReaderSelectionManager selectionManager) {
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
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            canvas.drawRect(pageInfo.getDisplayRect(), paint);
        }
    }

    private void initPaintWithAuxiliaryLineStyle(final Paint paint) {
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        PathEffect effect = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
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

    private void drawSearchResults(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, SingletonSharedPreference.AnnotationHighlightStyle highlightStyle) {
        drawReaderSelections(context, canvas, paint, viewInfo, userDataInfo.getSearchResults(), highlightStyle);
    }

    private void drawHighlightResult(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, ReaderSelectionManager selectionManager, SingletonSharedPreference.AnnotationHighlightStyle highlightStyle) {
        if (userDataInfo.hasHighlightResult()) {
            drawReaderSelection(context, canvas, paint, viewInfo, userDataInfo.getHighlightResult(), highlightStyle);
            drawSelectionCursor(canvas, paint, xorMode, selectionManager);
        }
    }

    private void drawAnnotations(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, SingletonSharedPreference.AnnotationHighlightStyle highlightStyle) {
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            if (userDataInfo.hasPageAnnotations(pageInfo)) {
                List<PageAnnotation> annotations = userDataInfo.getPageAnnotations(pageInfo);
                for (PageAnnotation annotation : annotations) {
                    drawHighlightRectangles(context, canvas, RectUtils.mergeRectanglesByBaseLine(annotation.getRectangles()), highlightStyle);
                    String note = annotation.getAnnotation().getNote();
                    if (!StringUtils.isNullOrEmpty(note)) {
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

    private void drawBookmark(Context context, Canvas canvas, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        Bitmap bitmap = BookmarkIconFactory.getBookmarkIcon(context, hasBookmark(userDataInfo, viewInfo));
        final Point point = BookmarkIconFactory.bookmarkPosition(canvas.getWidth(), bitmap);
        float left = AppCompatUtils.calculateEvenDigital(point.x);
        float top = AppCompatUtils.calculateEvenDigital(point.y);
        canvas.drawBitmap(bitmap, left, top, null);
    }

    private void drawReaderSelection(Context context, Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, ReaderSelection selection, SingletonSharedPreference.AnnotationHighlightStyle highlightStyle) {
        PageInfo pageInfo = viewInfo.getPageInfo(selection.getPagePosition());
        if (pageInfo != null) {
            drawHighlightRectangles(context, canvas, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()), highlightStyle);
        }
    }

    private void drawReaderSelections(Context context, Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, List<ReaderSelection> list, SingletonSharedPreference.AnnotationHighlightStyle highlightStyle) {
        if (list == null || list.size() <= 0) {
            return;
        }
        for (ReaderSelection sel : list) {
            drawReaderSelection(context, canvas, paint, viewInfo, sel, highlightStyle);
        }
    }

    private void drawHighlightRectangles(Context context, Canvas canvas, List<RectF> rectangles, SingletonSharedPreference.AnnotationHighlightStyle highlightStyle) {
        if (rectangles == null) {
            return;
        }
        Paint paint = new Paint();
        switch (highlightStyle) {
            case Underline:
                drawUnderLineHighlightRectangles(canvas, paint, rectangles);
                break;
            case Highlight:
                drawFillHighlightRectangles(canvas, paint, rectangles);
                break;
        }
    }

    private void drawUnderLineHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        int size = rectangles.size();
        for (int i = 0; i < size; ++i) {
            canvas.drawLine(rectangles.get(i).left, rectangles.get(i).bottom, rectangles.get(i).right, rectangles.get(i).bottom, paint);
        }
    }

    private void drawFillHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setXfermode(xorMode);
        int size = rectangles.size();
        for (int i = 0; i < size; ++i) {
            canvas.drawRect(rectangles.get(i), paint);
        }
    }

    private void drawHighLightSign(Context context, Canvas canvas, Paint paint, List<RectF> rectangles) {
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

    private boolean hasBookmark(final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        for (PageInfo pageInfo : viewInfo.getVisiblePages()) {
            if (userDataInfo.hasBookmark(pageInfo)) {
                return true;
            }
        }
        return false;
    }

    private void drawTestTouchPointCircle(Context context, Canvas canvas, Paint paint, final ReaderUserDataInfo userDataInfo) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        PointF touchPoint = userDataInfo.getTouchPoint();
        if (debugTestTouchPointCircle && touchPoint != null) {
            canvas.drawCircle(touchPoint.x, touchPoint.y, 20, paint);
            if (debugTestOffsetTouchPointCircle) {
                float offset = context.getResources().getDimension(R.dimen.move_point_offset_height);
                canvas.drawCircle(touchPoint.x, touchPoint.y - offset, 20, paint);
            }
        }
    }

    public void setAnnotationHighlightStyle(SingletonSharedPreference.AnnotationHighlightStyle annotationHighlightStyle) {
        this.annotationHighlightStyle = annotationHighlightStyle;
    }
}
