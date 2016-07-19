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
            public void requestPreview(final int pageStart, final int pageEnd, final Size desiredSize) {
                for (int i = pageStart; i <= pageEnd; i++) {
                    final int page = i;
                    int width = readerActivity.getDisplayWidth();
                    int height = readerActivity.getDisplayHeight();
                    if (!readerActivity.getReader().getRendererFeatures().supportScale()) {
                        width = desiredSize.width;
                        height = desiredSize.height;
                    }
                    final ReaderBitmapImpl bitmap = new ReaderBitmapImpl(width, height, Bitmap.Config.ARGB_8888);
                    RenderThumbnailRequest thumbnailRequest = new RenderThumbnailRequest(PagePositionUtils.fromPageNumber(page), bitmap);
                    readerActivity.getReader().submitRequest(readerActivity, thumbnailRequest, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            dialogQuickPreview.updatePreview(page, bitmap.getBitmap());
                            bitmap.recycleBitmap();
                        }
                    });
                }
            }
        });
        dialogQuickPreview.show();
    }
}
