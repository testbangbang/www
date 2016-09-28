package com.onyx.kreader.ui.actions;

import android.graphics.*;
import android.view.SurfaceHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.request.PanRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class PanAction extends BaseAction {

    private int offsetX, offsetY;
    static private Matrix translateMatrix = new Matrix();

    public PanAction(int x, int y) {
        offsetX = x;
        offsetY = y;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        translateMatrix.reset();
        ReaderDeviceManager.exitAnimationUpdate(false);
        final BaseReaderRequest request = new PanRequest(offsetX, offsetY);
        readerDataHolder.submitRenderRequest(request, callback);
    }

    public static void panning(final ReaderDataHolder readerDataHolder, int offsetX, int offsetY) {
        fastRedrawPanningBitmap(readerDataHolder, offsetX, offsetY);
    }

    static public void fastRedrawPanningBitmap(final ReaderDataHolder readerDataHolder, int dx, int dy) {
        ReaderActivity readerActivity = (ReaderActivity) readerDataHolder.getContext();
        ReaderDeviceManager.enterAnimationUpdate(true);
        final SurfaceHolder holder = readerActivity.getHolder();
        Canvas canvas =  holder.lockCanvas();
        Bitmap bmp = readerDataHolder.getReader().getViewportBitmap().getBitmap();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, readerDataHolder.getDisplayWidth(), readerDataHolder.getDisplayHeight(), paint);
        translateMatrix.setTranslate(dx, dy);
        canvas.drawBitmap(bmp, translateMatrix, null);
        holder.unlockCanvasAndPost(canvas);
    }
}
