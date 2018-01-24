package com.onyx.jdread.reader.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;

import java.util.List;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class ReaderViewHelper {
    private SurfaceView contentView;
    private Paint paint = new Paint();
    private static final int DEFAULT_MULTIPLEX = 1;
    public float dpiMultiplex = 1.0f;

    public ReaderViewHelper(Context context) {
        initData(context);
    }

    private void initData(Context context) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(11 * (context != null ? dpiMultiplex : DEFAULT_MULTIPLEX));
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        paint.setStrokeWidth(0);
    }

    public SurfaceView getContentView() {
        return contentView;
    }

    public void setReadPageView(SurfaceView contentView) {
        this.contentView = contentView;
    }

    public int getContentWidth() {
        return contentView.getWidth();
    }

    public int getContentHeight() {
        return contentView.getHeight();
    }

    public void updatePageView(Reader reader, ReaderUserDataInfo readerUserDataInfo, ReaderViewInfo readerViewInfo) {
        updatePageView(reader, readerUserDataInfo,readerViewInfo, null);
    }

    public void updatePageView(Reader reader, ReaderUserDataInfo readerUserDataInfo,ReaderViewInfo readerViewInfo, ReaderSelectionHelper readerSelectionManager) {
        try {
            ReaderDrawContext context = ReaderDrawContext.create(false);
            reader.getReaderHelper().getReaderLayoutManager().drawVisiblePages(reader, context, readerViewInfo);
            renderAll(reader, context.renderingBitmap.getBitmap(), readerUserDataInfo,readerViewInfo, readerSelectionManager);

            reader.getReaderHelper().saveToCache(context.renderingBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderAll(Reader reader, Bitmap bitmap, ReaderUserDataInfo readerUserDataInfo,final ReaderViewInfo readerViewInfo, ReaderSelectionHelper readerSelectionManager) {
        if (contentView == null) {
            return;
        }
        if (bitmap == null) {
            return;
        }
        paint.setDither(true);
        Canvas canvas = contentView.getHolder().lockCanvas();

        drawPageContent(canvas,bitmap);
        drawPageAnnotations(canvas,reader,readerUserDataInfo,readerViewInfo);
        drawHighlightResult(null, canvas, paint, reader, readerViewInfo, readerSelectionManager);

        contentView.getHolder().unlockCanvasAndPost(canvas);
    }

    public void drawPageContent(Canvas canvas,Bitmap bitmap){
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    public void showTouchFunctionRegion(Canvas canvas, Context context) {
        Paint.Style oldStyle = paint.getStyle();
        paint.setStyle(Paint.Style.STROKE);
        int oldColor = paint.getColor();
        paint.setColor(Color.BLACK);

        Rect rect = ShowSettingMenuAction.getRegionOne(context);
        canvas.drawRect(rect, paint);
        rect = ShowSettingMenuAction.getRegionTwo(context);
        canvas.drawRect(rect, paint);
        rect = PrevPageAction.getRegionOne(context);
        canvas.drawRect(rect, paint);
        rect = NextPageAction.getRegionOne(context);
        canvas.drawRect(rect, paint);
        rect = NextPageAction.getRegionTwo(context);
        canvas.drawRect(rect, paint);

        paint.setColor(oldColor);
        paint.setStyle(oldStyle);
    }

    private void drawHighlightResult(Context context, Canvas canvas, Paint paint, final Reader reader, final ReaderViewInfo readerViewInfo,
                                     ReaderSelectionHelper readerSelectionManager) {
        if(readerSelectionManager != null) {
            String pagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
            ReaderSelection readerSelection = readerSelectionManager.getCurrentSelection(pagePosition);
            if (readerViewInfo != null && readerSelection != null) {
                drawReaderSelection(context, canvas, paint, readerViewInfo, readerSelection);
                drawSelectionCursor(canvas, paint, readerSelectionManager, pagePosition);
            }
        }
    }

    private void drawReaderSelection(Context context, Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, ReaderSelection selection) {
        PageInfo pageInfo = viewInfo.getPageInfo(selection.getPagePosition());
        if (pageInfo != null) {
            drawHighlightRectangles(context, canvas, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()));
        }
    }

    private void drawSelectionCursor(Canvas canvas, Paint paint, ReaderSelectionHelper selectionManager, String pagePosition) {
        selectionManager.draw(pagePosition, canvas, paint);
    }

    private void drawHighlightRectangles(Context context, Canvas canvas, List<RectF> rectangles) {
        if (rectangles == null) {
            return;
        }
        Paint paint = new Paint();

        drawUnderLineHighlightRectangles(canvas, paint, rectangles);
    }

    private void drawUnderLineHighlightRectangles(Canvas canvas, Paint paint, List<RectF> rectangles) {
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        int size = rectangles.size();
        for (int i = 0; i < size; ++i) {
            canvas.drawLine(rectangles.get(i).left, rectangles.get(i).bottom, rectangles.get(i).right, rectangles.get(i).bottom, paint);
        }
    }

    private void drawPageAnnotations(Canvas canvas,Reader reader,ReaderUserDataInfo readerUserDataInfo,ReaderViewInfo readerViewInfo){
        readerUserDataInfo.loadPageAnnotations(reader.getReaderHelper().getContext(),
                reader.getReaderHelper().getRendererFeatures().supportScale(),
                reader.getReaderHelper().getPlugin().displayName(),
                reader.getReaderHelper().getDocumentMd5(),
                reader.getReaderHelper().getNavigator(),
                readerViewInfo.getVisiblePages());
        for (PageInfo pageInfo : readerViewInfo.getVisiblePages()) {
            if (readerUserDataInfo.hasPageAnnotations(pageInfo)) {
                List<PageAnnotation> annotations = readerUserDataInfo.getPageAnnotations(pageInfo);
                for (PageAnnotation annotation : annotations) {
                    drawHighlightRectangles(reader.getReaderHelper().getContext(), canvas, RectUtils.mergeRectanglesByBaseLine(annotation.getRectangles()));
                }
            }
        }

    }
}
