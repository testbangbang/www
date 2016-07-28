package com.onyx.kreader.ui.actions;

import android.content.IntentFilter;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import com.onyx.android.cropimage.CropImage;
import com.onyx.android.cropimage.CropImageResultReceiver;
import com.onyx.android.cropimage.data.CropArgs;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.request.ScaleByRectRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.io.File;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class SelectionScaleAction extends BaseAction {
    public static abstract class Callback {
        public abstract void onSelectionFinished(final ReaderDataHolder readerDataHolder, final CropArgs args);
    }

    private CropArgs cropArgs;
    private Callback callback;
    private CropImageResultReceiver selectionZoomAreaReceiver;

    public SelectionScaleAction() {
        cropArgs = new CropArgs();
        cropArgs.manualCropPage = true;
        cropArgs.manualSplitPage = false;
        callback = new Callback() {
            @Override
            public void onSelectionFinished(final ReaderDataHolder readerDataHolder, final CropArgs args) {
                scaleByRect(readerDataHolder, new RectF(args.selectionRect));
            }
        };
    }

    public SelectionScaleAction(CropArgs args, Callback callback) {
        this.cropArgs = args;
        this.callback = callback;
    }

    public void execute(final ReaderDataHolder readerDataHolder) {
        scaleByRect(readerDataHolder);
    }

    private void scaleByRect(final ReaderDataHolder readerDataHolder) {
        showSelectionActivity(readerDataHolder, cropArgs, new CropImage.SelectionCallback() {
            @Override
            public void onSelectionFinished(CropArgs args) {
                if (callback != null) {
                    callback.onSelectionFinished(readerDataHolder, args);
                }
            }
         });
    }

    private void showSelectionActivity(final ReaderDataHolder readerDataHolder, final CropArgs args, final CropImage.SelectionCallback callback) {
        Uri outputUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "cropped.png"));
        IntentFilter filter = new IntentFilter();
        filter.addAction(CropImage.INTENT_ACTION_SELECT_ZOOM_RECT);

        if (selectionZoomAreaReceiver != null) {
            readerDataHolder.getContext().unregisterReceiver(selectionZoomAreaReceiver);
        }
        selectionZoomAreaReceiver = new CropImageResultReceiver() {
            @Override
            public void onSelectionFinished(final CropArgs navigationArgs) {
                if (callback != null) {
                    callback.onSelectionFinished(navigationArgs);
                }
                resetEventListener(readerDataHolder);
            }
        };
        readerDataHolder.getContext().registerReceiver(selectionZoomAreaReceiver, filter);
        CropImage crop = new CropImage(readerDataHolder.getReader().getViewportBitmap().getBitmap());
        crop.output(outputUri).start((ReaderActivity)readerDataHolder.getContext(), false, false, false, args);
    }

    private void resetEventListener(final ReaderDataHolder readerDataHolder) {
        if (selectionZoomAreaReceiver != null) {
            readerDataHolder.getContext().unregisterReceiver(selectionZoomAreaReceiver);
            selectionZoomAreaReceiver = null;
        }
    }

    private void scaleByRect(final ReaderDataHolder readerDataHolder, final RectF rect) {
        final PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getVisiblePages().get(0);
        RectF docRect = ScaleByRectRequest.rectInDocument(pageInfo, rect);
        final ScaleByRectRequest request = new ScaleByRectRequest(readerDataHolder.getCurrentPageName(), docRect);
        readerDataHolder.submitRequest(request);
    }

}
