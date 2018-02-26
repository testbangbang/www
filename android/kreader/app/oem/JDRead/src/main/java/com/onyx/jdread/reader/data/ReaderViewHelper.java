package com.onyx.jdread.reader.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.api.ReaderHitTestManager;
import com.onyx.android.sdk.reader.api.ReaderNavigator;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.reader.common.PageAnnotation;
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.common.ReaderViewConfig;
import com.onyx.jdread.reader.epd.ReaderEpdHelper;
import com.onyx.jdread.reader.highlight.ReaderSelectionHelper;
import com.onyx.jdread.reader.menu.common.ReaderConfig;
import com.onyx.jdread.util.TimeUtils;

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
        return ReaderViewConfig.getContentWidth(contentView);
    }

    public int getContentHeight() {
        return ReaderViewConfig.getContentHeight(contentView);
    }

    public void updatePageView(Reader reader, ReaderUserDataInfo readerUserDataInfo, ReaderViewInfo readerViewInfo) {
        updatePageView(reader, readerUserDataInfo, readerViewInfo, null);
    }

    public void updatePageView(Reader reader, ReaderUserDataInfo readerUserDataInfo, ReaderViewInfo readerViewInfo, ReaderSelectionHelper readerSelectionManager) {
        try {
            ReaderDrawContext context = ReaderDrawContext.create(false);
            reader.getReaderHelper().getReaderLayoutManager().drawVisiblePages(reader, context, readerViewInfo);
            loadUserData(reader, readerUserDataInfo, readerViewInfo);
            renderAll(reader, context.renderingBitmap.getBitmap(), readerUserDataInfo, readerViewInfo, readerSelectionManager);

            reader.getReaderHelper().saveToCache(context.renderingBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadUserData(Reader reader, ReaderUserDataInfo readerUserDataInfo, ReaderViewInfo readerViewInfo) {
        readerUserDataInfo.setDocumentPath(reader.getDocumentInfo().getBookPath());
        readerUserDataInfo.setDocumentCategory(reader.getReaderHelper().getDocumentOptions().getDocumentCategory());
        readerUserDataInfo.setDocumentCodePage(reader.getReaderHelper().getDocumentOptions().getCodePage());
        readerUserDataInfo.setChineseConvertType(reader.getReaderHelper().getDocumentOptions().getChineseConvertType());
        readerUserDataInfo.setDocumentMetadata(reader.getReaderHelper().getDocumentMetadata());


        boolean isSupportScale = reader.getReaderHelper().getRendererFeatures().supportScale();
        String displayName = reader.getReaderHelper().getPlugin().displayName();
        String md5 = reader.getReaderHelper().getDocumentMd5();
        ReaderNavigator navigator = reader.getReaderHelper().getNavigator();

        Context context = reader.getReaderHelper().getContext();
        if (readerViewInfo != null) {
            readerUserDataInfo.loadPageBookmarks(context, isSupportScale, displayName, md5, navigator, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null) {
            readerUserDataInfo.loadPageLinks(context, navigator, readerViewInfo.getVisiblePages());
        }
        if (readerViewInfo != null) {
            readerUserDataInfo.loadPageImages(context, navigator, readerViewInfo.getVisiblePages());
        }

        if (readerViewInfo != null) {
            readerUserDataInfo.loadPageAnnotations(context, isSupportScale, displayName, md5, navigator, readerViewInfo.getVisiblePages());
        }
    }

    public void renderAll(Reader reader, Bitmap bitmap, ReaderUserDataInfo readerUserDataInfo, final ReaderViewInfo readerViewInfo, ReaderSelectionHelper readerSelectionManager) {
        if (contentView == null) {
            return;
        }
        if (bitmap == null) {
            return;
        }
        paint.setDither(true);
        applyEpdUpdate(reader, contentView);
        Canvas canvas = contentView.getHolder().lockCanvas();
        if(canvas == null){
            return;
        }
        try {
            drawPageContent(canvas, bitmap);
            drawPageAnnotations(canvas, reader, readerUserDataInfo, readerViewInfo);
            drawHighlightResult(null, canvas, paint, reader, readerViewInfo, readerSelectionManager);
            drawSearchResults(canvas,reader,readerUserDataInfo,readerViewInfo);
            drawTime(canvas, reader, readerViewInfo);
            drawPageNumber(canvas, reader, readerViewInfo);
        } finally {
            contentView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    public void drawPageContent(Canvas canvas, Bitmap bitmap) {
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
        if (readerSelectionManager != null) {
            String pagePosition = reader.getReaderHelper().getReaderLayoutManager().getCurrentPagePosition();
            ReaderSelection readerSelection = readerSelectionManager.getCurrentSelection(pagePosition);
            if (readerViewInfo != null && readerSelection != null) {
                drawReaderSelection(context, canvas, paint, readerViewInfo, readerSelection,false);
                drawSelectionCursor(canvas, paint, readerSelectionManager, pagePosition);
            }
        }
    }

    private void drawReaderSelection(Context context, Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, ReaderSelection selection,boolean annotationHighlightStyle) {
        PageInfo pageInfo = viewInfo.getPageInfo(selection.getPagePosition());
        if (pageInfo != null) {
            if(annotationHighlightStyle){
                drawFillHighlightRectangles(canvas, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()));
            }else {
                drawHighlightRectangles(context, canvas, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()));
            }
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

    private void drawFillHighlightRectangles(Canvas canvas, List<RectF> rectangles){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setColorFilter(new ColorMatrixColorFilter(getColorMatrix()));
        int size = rectangles.size();
        for (int i = 0; i < size; ++i) {
            canvas.drawRect(rectangles.get(i), paint);
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

    private void drawPageAnnotations(Canvas canvas, Reader reader, ReaderUserDataInfo readerUserDataInfo, ReaderViewInfo readerViewInfo) {
        readerUserDataInfo.loadPageAnnotations(reader.getReaderHelper().getContext(),
                reader.getReaderHelper().getRendererFeatures().supportScale(),
                reader.getReaderHelper().getPlugin().displayName(),
                reader.getReaderHelper().getDocumentMd5(),
                reader.getReaderHelper().getNavigator(),
                readerViewInfo.getVisiblePages());
        if (!reader.getReaderHelper().getRendererFeatures().supportScale()) {
            updateAnnotationRectangles(reader,readerViewInfo,readerUserDataInfo);
        }
        for (PageInfo pageInfo : readerViewInfo.getVisiblePages()) {
            if (readerUserDataInfo.hasPageAnnotations(pageInfo)) {
                List<PageAnnotation> annotations = readerUserDataInfo.getPageAnnotations(pageInfo);
                for (PageAnnotation annotation : annotations) {
                    drawHighlightRectangles(reader.getReaderHelper().getContext(), canvas, RectUtils.mergeRectanglesByBaseLine(annotation.getRectangles()));
                    String note = annotation.getAnnotation().getNote();
                    if (!StringUtils.isNullOrEmpty(note)){
                        drawHighLightSign(reader.getReaderHelper().getContext(), canvas, paint, annotation.getRectangles());
                    }
                }
            }
        }
    }

    private void updateAnnotationRectangles(final Reader reader,final ReaderViewInfo readerViewInfo,final ReaderUserDataInfo readerUserDataInfo) {
        for (PageInfo pageInfo : readerViewInfo.getVisiblePages()) {
            List<PageAnnotation> annotations = readerUserDataInfo.getPageAnnotations(pageInfo);
            if (annotations == null) {
                continue;
            }
            for (PageAnnotation annotation : annotations) {
                ReaderHitTestManager hitTestManager = reader.getReaderHelper().getHitTestManager();
                ReaderSelection selection = hitTestManager.selectOnScreen(pageInfo.getPosition(),
                        annotation.getAnnotation().getLocationBegin(),
                        annotation.getAnnotation().getLocationEnd());
                annotation.getRectangles().clear();
                if (selection != null) {
                    annotation.getRectangles().addAll(selection.getRectangles());
                }
            }
        }
    }

    private void drawHighLightSign(Context context, Canvas canvas, Paint paint, List<RectF> rectangles){
        if (rectangles == null || rectangles.size() < 1) {
            return;
        }
        RectF end = rectangles.get(rectangles.size() - 1);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_read_note);
        canvas.drawBitmap(bitmap, end.right - ReaderConfig.SIGN_RIGHT_MARGIN, (end.top - bitmap.getHeight() / 2), null);
    }


    private void drawSearchResults(Canvas canvas, Reader reader, ReaderUserDataInfo readerUserDataInfo, ReaderViewInfo readerViewInfo){
        List<ReaderSelection> searchResults = readerUserDataInfo.getSearchResults();
        if (searchResults == null || searchResults.size() <= 0) {
            return;
        }
        for (ReaderSelection sel : searchResults) {
            drawReaderSelection(reader.getReaderHelper().getContext(),canvas, paint, readerViewInfo, sel,false);
        }
    }

    public void drawTime(Canvas canvas, Reader reader, ReaderViewInfo readerViewInfo) {
        float textSize = paint.getTextSize();
        int textColor = paint.getColor();
        paint.setColor(0xff808080);
        paint.setTextSize(ReaderViewConfig.getTimeFontSize());
        String time = TimeUtils.getCurrentTime();
        PointF timePoint = ReaderViewConfig.getTimePoint(contentView);

        Rect bounds = new Rect();

        paint.getTextBounds(time, 0, time.length(), bounds);
        float x = timePoint.x;
        float y = timePoint.y - bounds.height();

        canvas.drawText(time, x, y, paint);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
    }

    public void drawPageNumber(Canvas canvas, Reader reader, ReaderViewInfo readerViewInfo) {
        float textSize = paint.getTextSize();
        int textColor = paint.getColor();
        paint.setColor(0xff808080);
        paint.setTextSize(ReaderViewConfig.getPageNumberFontSize());
        int currentPage = PagePositionUtils.getPageNumber(readerViewInfo.getFirstVisiblePage().getName()) + 1;
        int totalPage = readerViewInfo.getTotalPage();
        String page = currentPage + "/" + totalPage;
        PointF timePoint = ReaderViewConfig.getPageNumberPoint(contentView);

        float textWidth = paint.measureText(page);

        Rect bounds = new Rect();

        paint.getTextBounds(page, 0, page.length(), bounds);

        float x = timePoint.x - textWidth;
        float y = timePoint.y - bounds.height();
        canvas.drawText(page, x, y, paint);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
    }

    private void applyEpdUpdate(final Reader reader, final SurfaceView view) {
        reader.getReaderEpdHelper().applyWithGCInterval(view);
    }

    private void resetEpdUpdate(final Reader reader,final SurfaceView view){
        reader.getReaderEpdHelper().setGcInterval(ReaderEpdHelper.DEFAULT_GC_INTERVAL);
        ReaderEpdHelper.resetUpdateMode(view);
    }

    private ColorMatrix getColorMatrix() {
        return new ColorMatrix(new float[] {
                -1,  0,  0,  0, 255,
                0, -1,  0,  0, 255,
                0,  0, -1,  0, 255,
                0,  0,  0,  1,   0
        });
    }
}
