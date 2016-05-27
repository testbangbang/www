package com.onyx.kreader.ui.actions;

import android.graphics.*;
import android.view.SurfaceHolder;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.device.ReaderDeviceManager;
import com.onyx.kreader.host.request.PanRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderConfig;

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

    public void execute(final ReaderActivity readerActivity) {
        translateMatrix.reset();
        ReaderDeviceManager.exitAnimationUpdate(false);
        final BaseRequest request = new PanRequest(offsetX, offsetY);
        readerActivity.submitRenderRequest(request);
    }

    public static void panning(final ReaderActivity readerActivity, int offsetX, int offsetY) {
        fastRedrawPanningBitmap(readerActivity, offsetX, offsetY);
    }

    static public void fastRedrawPanningBitmap(final ReaderActivity readerActivity, int dx, int dy) {
        ReaderDeviceManager.enterAnimationUpdate(true);
        final SurfaceHolder holder = readerActivity.getHolder();
        Canvas canvas =  holder.lockCanvas();
        Bitmap bmp = readerActivity.getReader().getViewportBitmap().getBitmap();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, readerActivity.getDisplayWidth(), readerActivity.getDisplayHeight(), paint);
        translateMatrix.setTranslate(dx, dy);
        canvas.drawBitmap(bmp, translateMatrix, null);
        holder.unlockCanvasAndPost(canvas);
    }
}
