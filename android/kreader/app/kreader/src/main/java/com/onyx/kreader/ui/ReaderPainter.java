package com.onyx.kreader.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelXorXfermode;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.scribble.NoteViewHelper;
import com.onyx.android.sdk.scribble.request.ShapeDataInfo;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.kreader.R;
import com.onyx.kreader.api.ReaderSelection;
import com.onyx.kreader.common.PageAnnotation;
import com.onyx.kreader.common.ReaderUserDataInfo;
import com.onyx.kreader.common.ReaderViewInfo;
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

    private enum DrawHighlightPaintStyle {UnderLine, Fill}

    public ReaderPainter() {
    }

    public void drawPage(Context context, Canvas canvas, final Bitmap bitmap, final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo, ReaderSelectionManager selectionManager, NoteViewHelper noteViewHelper, ShapeDataInfo shapeDataInfo) {
        Paint paint = new Paint();
        drawBackground(canvas, paint);
        drawBitmap(canvas, paint, bitmap);
        drawSearchResults(canvas, paint, userDataInfo, viewInfo, DrawHighlightPaintStyle.Fill);
        drawHighlightResult(canvas, paint, userDataInfo, viewInfo, selectionManager, DrawHighlightPaintStyle.UnderLine);
        if (SingletonSharedPreference.isShowAnnotation(context)) {
            drawAnnotations(context, canvas, paint, userDataInfo, viewInfo, DrawHighlightPaintStyle.UnderLine);
        }
        if (SingletonSharedPreference.isShowBookmark(context)) {
            drawBookmark(context, canvas, userDataInfo, viewInfo);
        }
        drawShapes(canvas, paint, noteViewHelper, shapeDataInfo);
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
            canvas.drawLine(rectangles.get(i).left,rectangles.get(i).bottom,rectangles.get(i).right,rectangles.get(i).bottom,paint);
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

    private void drawShapes(final Canvas canvas, Paint paint, NoteViewHelper noteViewHelper, ShapeDataInfo shapeDataInfo) {
        if (true || !isShapeBitmapReady(noteViewHelper, shapeDataInfo)) {
            return;
        }
        final Bitmap bitmap = noteViewHelper.getViewBitmap();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.XOR));
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    private boolean hasBookmark(final ReaderUserDataInfo userDataInfo, final ReaderViewInfo viewInfo) {
        return userDataInfo.hasBookmark(viewInfo.getFirstVisiblePage());
    }

    private boolean hasShapes(ShapeDataInfo shapeDataInfo) {
        if (shapeDataInfo == null) {
            return false;
        }
        return shapeDataInfo.hasShapes();
    }

    private boolean isShapeBitmapReady(NoteViewHelper noteViewHelper, ShapeDataInfo shapeDataInfo) {
        if (!hasShapes(shapeDataInfo)) {
            return false;
        }

        final Bitmap bitmap = noteViewHelper.getViewBitmap();
        if (bitmap == null) {
            return false;
        }
        return true;
    }
}
