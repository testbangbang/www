package com.onyx.kreader.ui.actions;

import android.content.IntentFilter;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.request.ScaleByRectRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.android.cropimage.CropImage;
import com.onyx.android.cropimage.CropImageResultReceiver;
import com.onyx.android.cropimage.data.CropArgs;


import java.io.File;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class SelectionScaleAction extends BaseAction {

    private CropImageResultReceiver selectionZoomAreaReceiver;

    public void execute(final ReaderActivity readerActivity) {
        scaleByRect(readerActivity);
    }

    private void scaleByRect(final ReaderActivity readerActivity) {
        CropArgs args = new CropArgs();
        args.manualCropPage = true;
        showSelectionActivity(readerActivity, args, new CropImage.SelectionCallback() {
            @Override
            public void onSelectionFinished(CropArgs args) {
                scaleByRect(readerActivity, new RectF(args.selectionRect));
            }
         });
    }

    private void showSelectionActivity(final ReaderActivity readerActivity, final CropArgs args, final CropImage.SelectionCallback callback) {
        Uri outputUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "cropped.png"));
        IntentFilter filter = new IntentFilter();
        filter.addAction(CropImage.INTENT_ACTION_SELECT_ZOOM_RECT);

        if (selectionZoomAreaReceiver != null) {
            readerActivity.unregisterReceiver(selectionZoomAreaReceiver);
        }
        selectionZoomAreaReceiver = new CropImageResultReceiver() {
            @Override
            public void onSelectionFinished(final CropArgs navigationArgs) {
                if (callback != null) {
                    callback.onSelectionFinished(navigationArgs);
                }
                resetEventListener(readerActivity);
            }
        };
        readerActivity.registerReceiver(selectionZoomAreaReceiver, filter);
        CropImage crop = new CropImage(readerActivity.getReader().getViewportBitmap().getBitmap());
        crop.output(outputUri).start(readerActivity, false, false, false, args);
    }

    private void resetEventListener(final ReaderActivity readerActivity) {
        if (selectionZoomAreaReceiver != null) {
            readerActivity.unregisterReceiver(selectionZoomAreaReceiver);
            selectionZoomAreaReceiver = null;
        }
    }

    private void scaleByRect(final ReaderActivity readerActivity, final RectF rect) {
        final PageInfo pageInfo = readerActivity.getReaderViewInfo().getVisiblePages().get(0);
        RectF docRect = ScaleByRectRequest.rectInDocument(pageInfo, rect);
        final ScaleByRectRequest request = new ScaleByRectRequest(readerActivity.getCurrentPageName(), docRect);
        readerActivity.submitRequest(request);
    }

}
