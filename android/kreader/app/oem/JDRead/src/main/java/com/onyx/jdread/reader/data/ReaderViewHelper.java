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
import com.onyx.android.sdk.reader.common.ReaderDrawContext;
import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.utils.RectUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.reader.actions.NextPageAction;
import com.onyx.jdread.reader.actions.PrevPageAction;
import com.onyx.jdread.reader.actions.ShowSettingMenuAction;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.highlight.ReaderSelectionManager;

import java.util.List;

/**
 * Created by huxiaomao on 2017/12/22.
 */

public class ReaderViewHelper {
    private SurfaceView readPageView;
    private Paint paint = new Paint();
    private static final int DEFAULT_MULTIPLEX = 1;
    public float dpiMultiplex = 1.0f;

    public ReaderViewHelper() {
        initData();
    }

    private void initData() {
        paint.setColor(Color.BLACK);
        paint.setTextSize(11 * (JDReadApplication.getInstance() != null ? dpiMultiplex : DEFAULT_MULTIPLEX));
        paint.setAntiAlias(false);
        paint.setFilterBitmap(false);
        paint.setStrokeWidth(0);
    }

    public SurfaceView getReadPageView() {
        return readPageView;
    }

    public void setReadPageView(SurfaceView readPageView) {
        this.readPageView = readPageView;
    }

    public int getPageViewWidth() {
        return readPageView.getWidth();
    }

    public int getPageViewHeight() {
        return readPageView.getHeight();
    }

    public void updatePageView(ReaderDataHolder readerDataHolder, ReaderUserDataInfo readerUserDataInfo,ReaderViewInfo readerViewInfo) {
        try {
            ReaderDrawContext context = ReaderDrawContext.create(false);
            readerDataHolder.getReader().getReaderHelper().getReaderLayoutManager().drawVisiblePages(readerDataHolder.getReader(), context, readerViewInfo);
            draw(readerDataHolder, context.renderingBitmap.getBitmap(),readerUserDataInfo,readerViewInfo);

            readerDataHolder.getReader().getReaderHelper().transferRenderBitmapToViewport(context.renderingBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(ReaderDataHolder readerDataHolder, Bitmap bitmap,final ReaderUserDataInfo readerUserDataInfo,final ReaderViewInfo readerViewInfo) {
        if (readPageView == null) {
            return;
        }
        if (bitmap == null) {
            return;
        }
        paint.setDither(true);
        Canvas canvas = readPageView.getHolder().lockCanvas();
        canvas.drawColor(Color.WHITE);
        paint.setColor(Color.BLACK);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        drawHighlightResult(null, canvas, paint, readerDataHolder,readerUserDataInfo,readerViewInfo);
        readPageView.getHolder().unlockCanvasAndPost(canvas);
    }

    public void showTouchFunctionRegion(Canvas canvas) {
        Paint.Style oldStyle = paint.getStyle();
        paint.setStyle(Paint.Style.STROKE);
        int oldColor = paint.getColor();
        paint.setColor(Color.BLACK);

        Rect rect = ShowSettingMenuAction.getRegionOne();
        canvas.drawRect(rect, paint);
        rect = ShowSettingMenuAction.getRegionTwo();
        canvas.drawRect(rect, paint);
        rect = PrevPageAction.getRegionOne();
        canvas.drawRect(rect, paint);
        rect = NextPageAction.getRegionOne();
        canvas.drawRect(rect, paint);
        rect = NextPageAction.getRegionTwo();
        canvas.drawRect(rect, paint);

        paint.setColor(oldColor);
        paint.setStyle(oldStyle);
    }

    private void drawHighlightResult(Context context, Canvas canvas, Paint paint, final ReaderDataHolder readerDataHolder, final ReaderUserDataInfo readerUserDataInfo,final ReaderViewInfo readerViewInfo) {
        if (readerViewInfo != null && readerUserDataInfo != null && readerUserDataInfo.hasHighlightResult()) {
            readerDataHolder.getReaderSelectionManager().setCurrentSelection(readerUserDataInfo.getHighlightResult());
            readerDataHolder.getReaderSelectionManager().update(JDReadApplication.getInstance().getApplicationContext());

            readerDataHolder.getReaderSelectionManager().updateDisplayPosition();
            readerDataHolder.getReaderSelectionManager().setEnable(true);
            drawReaderSelection(context, canvas, paint, readerViewInfo, readerUserDataInfo.getHighlightResult());
            drawSelectionCursor(canvas, paint, readerDataHolder.getReaderSelectionManager());
        }
    }

    private void drawReaderSelection(Context context, Canvas canvas, Paint paint, final ReaderViewInfo viewInfo, ReaderSelection selection) {
        PageInfo pageInfo = viewInfo.getPageInfo(selection.getPagePosition());
        if (pageInfo != null) {
            drawHighlightRectangles(context, canvas, RectUtils.mergeRectanglesByBaseLine(selection.getRectangles()));
        }
    }

    private void drawSelectionCursor(Canvas canvas, Paint paint, ReaderSelectionManager selectionManager) {
        selectionManager.draw(canvas, paint);
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
}
