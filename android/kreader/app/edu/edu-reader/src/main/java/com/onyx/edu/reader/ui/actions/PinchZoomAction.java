package com.onyx.edu.reader.ui.actions;

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
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.edu.reader.R;
import com.onyx.edu.reader.device.ReaderDeviceManager;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.request.ScaleRequest;
import com.onyx.android.sdk.reader.host.request.ScaleToPageRequest;
import com.onyx.edu.reader.ui.ReaderActivity;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.data.SingletonSharedPreference;
import com.onyx.edu.reader.ui.events.PinchZoomEvent;

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

    static private float initialGestureSpan;
    static private float initialFontSize;
    static private float lastFontSize;

    private ScaleGestureDetector detector;

    public PinchZoomAction(final ScaleGestureDetector d) {
        detector = d;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        scaling(readerDataHolder, detector);
        BaseCallback.invoke(callback, null, null);
    }

    static public void scaleBegin(final ReaderDataHolder readerDataHolder, final ScaleGestureDetector detector) {
        if (readerDataHolder.supportScalable()) {
            scaleBeginForFixedPage(readerDataHolder, detector);
        } else if (readerDataHolder.supportFontSizeAdjustment()) {
            scaleBeginForFlowPage(readerDataHolder, detector);
        }
    }

    static public void scaling(final ReaderDataHolder readerDataHolder, final ScaleGestureDetector detector) {
        if (readerDataHolder.supportScalable()) {
            scalingForFixedPage(readerDataHolder, detector);
        } else if (readerDataHolder.supportFontSizeAdjustment()) {
            scalingForFlowPage(readerDataHolder, detector);
        }
    }

    static public void scaleEnd(final ReaderDataHolder readerDataHolder, BaseCallback callback) {
        if (readerDataHolder.supportScalable()) {
            scaleEndForFixedPage(readerDataHolder, callback);
        } else if (readerDataHolder.supportFontSizeAdjustment()) {
            scaleEndForFlowPage(readerDataHolder, callback);
        }
    }

    static private void scaleBeginForFixedPage(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        lastFocusX = detector.getFocusX();
        lastFocusY = detector.getFocusY();
        scaleMatrix.reset();
        initScale = readerDataHolder.getReaderViewInfo().getFirstVisiblePage().getActualScale();
        ReaderDeviceManager.enterAnimationUpdate(true);
        fastRedrawScalingBitmap(readerDataHolder);
    }

    static private void scalingForFixedPage(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
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
        showScalingInfo(readerDataHolder, scale);
        if (!animateDisplay) {
            return;
        }
        fastRedrawScalingBitmap(readerDataHolder);
    }

    static private void scaleEndForFixedPage(ReaderDataHolder readerDataHolder, BaseCallback callback) {
        hideScalingInfo(readerDataHolder);

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
            newScale = PageConstants.MAX_SCALE;
            pageInfo.setScale(newScale);
            Toast.makeText(readerDataHolder.getContext(),
                    R.string.max_scroll_toast, Toast.LENGTH_SHORT).show();
        }

        final ScaleRequest scaleRequest = new ScaleRequest(pageInfo.getName(), newScale, left, top);
        readerDataHolder.submitRenderRequest(scaleRequest, callback);

    }

    private static void scaleBeginForFlowPage(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        scaleMatrix.reset();
        initialGestureSpan = detector.getCurrentSpan();
        initialFontSize = readerDataHolder.getReaderViewInfo().getReaderTextStyle().getFontSize().getValue();
        lastFontSize = initialFontSize;
    }

    private static void scalingForFlowPage(ReaderDataHolder readerDataHolder, ScaleGestureDetector detector) {
        float newSize = initialFontSize * detector.getCurrentSpan() / initialGestureSpan;
        lastFontSize = ReaderTextStyle.limitFontSize(newSize);
        showFontSizeInfo(readerDataHolder, lastFontSize);
    }

    private static void scaleEndForFlowPage(ReaderDataHolder readerDataHolder, BaseCallback callback) {
        ReaderTextStyle style = readerDataHolder.getReaderViewInfo().getReaderTextStyle();
        style.setFontSize(ReaderTextStyle.SPUnit.create(lastFontSize));
        new ChangeStyleAction(style).execute(readerDataHolder, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e == null) {
                    SingletonSharedPreference.setLastFontSize(lastFontSize);
                }
            }
        });
        hideFontSizeInfo(readerDataHolder);
    }

    static void showFontSizeInfo(final ReaderDataHolder readerDataHolder, final float size) {
        readerDataHolder.updatePinchZoomMenu(PinchZoomEvent.create(PinchZoomEvent.Command.SHOW, PinchZoomEvent.Type.FONT_SIZE, (int)size));
    }

    static void hideFontSizeInfo(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.updatePinchZoomMenu(PinchZoomEvent.create(PinchZoomEvent.Command.HIDE, PinchZoomEvent.Type.FONT_SIZE, 0));
    }

    static void showScalingInfo(final ReaderDataHolder readerDataHolder, final float scale) {
        int s = (int)(scale * 100);
        readerDataHolder.updatePinchZoomMenu(PinchZoomEvent.create(PinchZoomEvent.Command.SHOW, PinchZoomEvent.Type.SCALE, s));
    }

    static void hideScalingInfo(final ReaderDataHolder readerDataHolder) {
        readerDataHolder.updatePinchZoomMenu(PinchZoomEvent.create(PinchZoomEvent.Command.HIDE, PinchZoomEvent.Type.SCALE, 0));
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

    static private float filterScale(float currentScale, float targetScale, Matrix matrix) {
        float tmp = targetScale - currentScale;
        if (Math.abs(tmp) >= 1) {
            matrix.reset();
            targetScale = targetScale - tmp;
        }
        return targetScale;
    }

}
