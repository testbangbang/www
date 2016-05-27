package com.onyx.kreader.ui.actions;

import android.graphics.*;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.request.ScaleRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderConfig;

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

    public void execute(final ReaderActivity readerActivity) {
        scaling(readerActivity, detector);
    }

    static public void scaleBegin(final ReaderActivity readerActivity, final ScaleGestureDetector detector) {
        lastFocusX = detector.getFocusX();
        lastFocusY = detector.getFocusY();
        scaleMatrix.reset();
        initScale = readerActivity.getReaderViewInfo().getFirstVisiblePage().getActualScale();
        //ReaderDeviceManager.sharedInstance().enterAnimationUpdate(surfaceView, Integer.MAX_VALUE, ReaderConfig.sharedInstance(this).getClearBeforeAnimation());
    }

    static public void scaling(final ReaderActivity readerActivity, final ScaleGestureDetector detector) {
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
        scale = filterScale(lastScale, scale, scaleMatrix);
        lastScale = scale;
        showScalingInfo(scale);
        if (!animateDisplay) {
            return;
        }
        fastRedrawScalingBitmap(readerActivity);
    }

    static void showScalingInfo(final float scale) {
        String string = String.format("%d %%", (int)(scale * 100));
//        getTextZoomingPopupMenu().showAndUpdate(TextZoomingPopupMenu.MessageToShown.ZoomFactor, string);
    }

    static public void fastRedrawScalingBitmap(final ReaderActivity readerActivity) {
        ReaderDeviceManager.enterAnimationUpdate(ReaderConfig.sharedInstance(readerActivity).getClearBeforeAnimation());
        final SurfaceHolder holder = readerActivity.getHolder();
        Canvas canvas =  holder.lockCanvas();
        Bitmap bmp = readerActivity.getReader().getViewportBitmap().getBitmap();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, readerActivity.getDisplayWidth(), readerActivity.getDisplayHeight(), paint);
        canvas.drawBitmap(bmp, scaleMatrix, null);
        holder.unlockCanvasAndPost(canvas);
    }



    static public void scaleEnd(final ReaderActivity readerActivity) {
        float values[] = new float[9];
        scaleMatrix.getValues(values);
        final PageInfo pageInfo = readerActivity.getReaderViewInfo().getFirstVisiblePage();
        float deltaScale =  values[Matrix.MSCALE_X];
        float deltaX = values[Matrix.MTRANS_X];// detector.getFocusX() - initFocusX;
        float deltaY = values[Matrix.MTRANS_Y];//detector.getFocusY() - initFocusY;

        // when scaling, we cannot scale shadow between pages. the value should be adjusted.
        // deltaScale * pageInfo.pageRectInScreen.left + deltaX is the final screen position
        // convert screen position to document position by
        float left = deltaScale * pageInfo.getPositionRect().left - (deltaScale * pageInfo.getPositionRect().left + deltaX);
        float top =  deltaScale * pageInfo.getPositionRect().top - (deltaScale * pageInfo.getPositionRect().top + deltaY);

//        hideTextZoomingPopupMenu();
//        handlerManager.setEnable(false);
        float newScale = pageInfo.getActualScale() * deltaScale;
        final ScaleRequest scaleRequest = new ScaleRequest(pageInfo.getName(), newScale, left, top);
        readerActivity.submitRenderRequest(scaleRequest);
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
