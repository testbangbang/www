package com.onyx.kreader.ui.actions;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.R;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.host.request.ScaleRequest;
import com.onyx.kreader.host.request.ScaleToPageRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/26/16.
 */
public class PinchZoomAction extends BaseAction {

    static private Matrix scaleMatrix = new Matrix();
    static private Matrix transformationMatrix = new Matrix();
    static private float lastFocusX;
    static private float lastFocusY;
    static private float initScale;
    static private float lastScale;
    static private boolean animateDisplay = true;
    static private boolean filterScaling = true;


    private ScaleGestureDetector detector;

    public PinchZoomAction(final ScaleGestureDetector d) {
        detector = d;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        scaling(readerDataHolder, detector);
        BaseCallback.invoke(callback, null, null);
    }

    static public void scaleBegin(final ReaderDataHolder readerDataHolder, final ScaleGestureDetector detector) {
        lastFocusX = detector.getFocusX();
        lastFocusY = detector.getFocusY();
        scaleMatrix.reset();
        initScale = readerDataHolder.getReaderViewInfo().getFirstVisiblePage().getActualScale();
        ReaderDeviceManager.enterAnimationUpdate(true);
    }

    static public void scaling(final ReaderDataHolder readerDataHolder, final ScaleGestureDetector detector) {
        float focusX = detector.getFocusX();
        float focusY = detector.getFocusY();
        transformationMatrix.reset();
        transformationMatrix.postTranslate(-focusX, -focusY);
        transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());
        float focusShiftX = focusX - lastFocusX;
        float focusShiftY = focusY - lastFocusY;
        transformationMatrix.postTranslate(focusX + focusShiftX, focusY + focusShiftY);
        scaleMatrix.postConcat(transformationMatrix);
        lastFocusX = focusX;
        lastFocusY = focusY;
        float values[] = new float[9];
        scaleMatrix.getValues(values);
        float scale =  values[Matrix.MSCALE_X] * initScale;
        if (Math.abs(lastScale - scale) <= 0.10 && filterScaling) {
            return;
        }
        lastScale = scale;
        showScalingInfo(scale);
        if (!animateDisplay) {
            return;
        }
        fastRedrawScalingBitmap(readerDataHolder);
    }

    static void showScalingInfo(final float scale) {
        String string = String.format("%d %%", (int)(scale * 100));
//        getTextZoomingPopupMenu().showAndUpdate(TextZoomingPopupMenu.MessageToShown.ZoomFactor, string);
    }

    static public void fastRedrawScalingBitmap(final ReaderDataHolder readerDataHolder) {
        ReaderActivity readerActivity = (ReaderActivity)readerDataHolder.getContext();
        final SurfaceHolder holder = readerActivity.getHolder();
        Canvas canvas =  holder.lockCanvas();
        Bitmap bmp = readerDataHolder.getReader().getViewportBitmap().getBitmap();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, readerDataHolder.getDisplayWidth(), readerDataHolder.getDisplayHeight(), paint);
        canvas.drawBitmap(bmp, scaleMatrix, null);
        holder.unlockCanvasAndPost(canvas);
    }

    static public void scaleEnd(final ReaderDataHolder readerDataHolder, BaseCallback callback) {
        float values[] = new float[9];
        scaleMatrix.getValues(values);
        final RectF viewport = readerDataHolder.getReaderViewInfo().viewportInDoc;
        final PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getFirstVisiblePage();
        float deltaScale =  values[Matrix.MSCALE_X];
        float deltaX = values[Matrix.MTRANS_X];
        float deltaY = values[Matrix.MTRANS_Y];

        // the final page position in document coordinates system should be
        // pageInfo.getPositionRect().left * deltaScale + deltaX
        float left = deltaScale * PageUtils.viewportInPage(pageInfo, viewport).left - deltaX;
        float top =  deltaScale * PageUtils.viewportInPage(pageInfo, viewport).top  - deltaY;

//        hideTextZoomingPopupMenu();
//        handlerManager.setEnable(false);

        ReaderDeviceManager.exitAnimationUpdate(false);
        float newScale = pageInfo.getActualScale() * deltaScale;
        pageInfo.setScale(newScale);

        if (deltaScale < 1.0f && !readerDataHolder.canCurrentPageScaleDown()) {
            Toast.makeText(readerDataHolder.getContext(),
                    R.string.min_scroll_toast, Toast.LENGTH_SHORT).show();
            readerDataHolder.submitRenderRequest(new ScaleToPageRequest(readerDataHolder.getCurrentPageName()), callback);
            return;
        } else if (deltaScale > 1.0f && !readerDataHolder.canCurrentPageScaleUp()) {
            Toast.makeText(readerDataHolder.getContext(),
                    R.string.max_scroll_toast, Toast.LENGTH_SHORT).show();
            readerDataHolder.submitRenderRequest(new RenderRequest(), callback);
            return;
        }

        final ScaleRequest scaleRequest = new ScaleRequest(pageInfo.getName(), newScale, left, top);
        readerDataHolder.submitRenderRequest(scaleRequest, callback);
    }

    static private float filterScale(float currentScale, float targetScale, Matrix matrix) {
        float tmp = targetScale - currentScale;
        if (Math.abs(tmp) >= 1) {
            matrix.reset();
            targetScale = targetScale - tmp;
        }
        return targetScale;
    }

}
