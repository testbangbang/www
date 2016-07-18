package com.onyx.kreader.ui.actions;

import android.graphics.Bitmap;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.Size;
import com.onyx.kreader.host.request.GotoLocationRequest;
import com.onyx.kreader.host.request.RenderRequest;
import com.onyx.kreader.ui.ReaderActivity;
import com.onyx.kreader.ui.dialog.DialogQuickPreview;

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
                    GotoLocationRequest gotoLocationRequest = new GotoLocationRequest(page);
                    readerActivity.getReader().submitRequest(readerActivity, gotoLocationRequest, new BaseCallback() {
                        @Override
                        public void done(BaseRequest request, Throwable e) {
                            dialogQuickPreview.updatePreview(page, readerActivity.getReader().getViewportBitmap().getBitmap());
                        }
                    });
                }
            }
        });
        dialogQuickPreview.show();
    }
}
