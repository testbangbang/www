package com.onyx.kreader.ui.actions;

import android.graphics.Bitmap;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.data.Size;
import com.onyx.kreader.host.request.RenderThumbnailRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogQuickPreview;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.List;

/**
 * Created by joy on 7/15/16.
 */
public class ShowQuickPreviewAction extends BaseAction {

    private DialogQuickPreview dialogQuickPreview;

    @Override
    public void execute(final ReaderActivity readerActivity) {
        dialogQuickPreview = new DialogQuickPreview(readerActivity,
                readerActivity.getPageCount(), readerActivity.getCurrentPage(),
                readerActivity.getReader().getViewportBitmap().getBitmap(), new DialogQuickPreview.Callback() {

            @Override
            public void requestPreview(final List<Integer> pages, final Size desiredSize) {
                requestPreviewBySequence(readerActivity, pages, desiredSize);
            }
        });
        dialogQuickPreview.show();
    }

    private void requestPreviewBySequence(final ReaderActivity readerActivity, final List<Integer> pages, final Size desiredSize) {
        if (pages.size() <= 0) {
            return;
        }
        final int current = pages.remove(0);
        int width = readerActivity.getDisplayWidth();
        int height = readerActivity.getDisplayHeight();
        if (!readerActivity.getReader().getRendererFeatures().supportScale()) {
            width = desiredSize.width;
            height = desiredSize.height;
        }
        final ReaderBitmapImpl bitmap = new ReaderBitmapImpl(width, height, Bitmap.Config.ARGB_8888);
        RenderThumbnailRequest thumbnailRequest = new RenderThumbnailRequest(PagePositionUtils.fromPageNumber(current), bitmap);
        readerActivity.getReader().submitRequest(readerActivity, thumbnailRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogQuickPreview.updatePreview(current, bitmap.getBitmap());
                bitmap.recycleBitmap();
                requestPreviewBySequence(readerActivity, pages, desiredSize);
            }
        });
    }
}
