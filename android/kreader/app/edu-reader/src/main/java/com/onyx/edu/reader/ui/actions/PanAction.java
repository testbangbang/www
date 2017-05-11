package com.onyx.edu.reader.ui.actions;

import android.graphics.*;
import android.view.SurfaceHolder;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.edu.reader.device.ReaderDeviceManager;
import com.onyx.android.sdk.reader.host.request.PanRequest;
import com.onyx.edu.reader.ui.ReaderActivity;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class PanAction extends BaseAction {

    private int offsetX, offsetY;
    private static Matrix translateMatrix = new Matrix();


    public PanAction(int x, int y) {
        offsetX = x;
        offsetY = y;
    }

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        ensureInNormalUpdateMode();
        translateMatrix.reset();
        final BaseReaderRequest request = new PanRequest(offsetX, offsetY);
        readerDataHolder.submitRenderRequest(request, callback);
    }

    public static void panning(final ReaderDataHolder readerDataHolder, int offsetX, int offsetY) {
        ensureInFastUpdateMode();
        fastRedrawPanningBitmap(readerDataHolder, offsetX, offsetY);
    }

    private static void ensureInFastUpdateMode() {
        ReaderDeviceManager.enterAnimationUpdate(true);
    }

    private static void ensureInNormalUpdateMode() {
        ReaderDeviceManager.exitAnimationUpdate(false);
    }

    public static void fastRedrawPanningBitmap(final ReaderDataHolder readerDataHolder, int dx, int dy) {
        ReaderActivity readerActivity = (ReaderActivity) readerDataHolder.getContext();
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
