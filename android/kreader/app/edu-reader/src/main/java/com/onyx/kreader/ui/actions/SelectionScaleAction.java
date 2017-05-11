package com.onyx.kreader.ui.actions;

import android.graphics.RectF;

import com.onyx.android.cropimage.CropImage;
import com.onyx.android.cropimage.CropImageDialog;
import com.onyx.android.cropimage.CropImageResultCallback;
import com.onyx.android.cropimage.CropImageResultReceiver;
import com.onyx.android.cropimage.data.CropArgs;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.host.request.ScaleByRectRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

/**
 * Created by zhuzeng on 5/18/16.
 */
public class SelectionScaleAction extends BaseAction {
    public static abstract class Callback {
        public abstract void onSelectionFinished(final ReaderDataHolder readerDataHolder, final CropArgs args);
    }

    private CropArgs cropArgs;
    private Callback callback;

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

    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        scaleByRect(readerDataHolder);
        BaseCallback.invoke(callback, null, null);
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
        new CropImageDialog(readerDataHolder.getContext(),
                readerDataHolder.getReader().getViewportBitmap().getBitmap(),
                args, new CropImageResultCallback() {
            @Override
            public void onSelectionFinished(CropArgs args) {
                if (callback != null) {
                    callback.onSelectionFinished(args);
                }
            }
        }).show();
    }

    private void scaleByRect(final ReaderDataHolder readerDataHolder, final RectF rect) {
        final PageInfo pageInfo = readerDataHolder.getReaderViewInfo().getVisiblePages().get(0);
        RectF docRect = ScaleByRectRequest.rectInDocument(pageInfo, rect);
        final ScaleByRectRequest request = new ScaleByRectRequest(readerDataHolder.getCurrentPageName(), docRect);
        readerDataHolder.submitRenderRequest(request);
    }

}
