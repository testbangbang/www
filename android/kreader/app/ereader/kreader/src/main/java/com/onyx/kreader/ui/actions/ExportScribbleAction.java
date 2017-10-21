package com.onyx.kreader.ui.actions;

import android.graphics.Bitmap;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.R;
import com.onyx.android.sdk.reader.host.request.ExportScribbleRequest;
import com.onyx.kreader.note.actions.GetScribbleBitmapAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.List;

/**
 * Created by ming on 16/10/17.
 */

public class ExportScribbleAction extends BaseAction {

    private List<PageInfo> requestPages;

    GetScribbleBitmapAction getScribbleBitmapAction;

    public ExportScribbleAction(List<PageInfo> requestPages) {
        this.requestPages = requestPages;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback baseCallback) {
        showLoadingDialog(readerDataHolder, R.string.exporting);

        int width = 600;
        int height = 800;
        if (readerDataHolder.isFixedPageDocument()) {
            width = readerDataHolder.getDisplayWidth();
            height = readerDataHolder.getDisplayHeight();
        }
        getScribbleBitmapAction = new GetScribbleBitmapAction(requestPages, width, height);
        getScribbleBitmapAction.execute(readerDataHolder, new GetScribbleBitmapAction.Callback() {
            @Override
            public void onNext(final PageInfo page, final Bitmap bitmap, PageInfo pageInfo) {
                final ExportScribbleRequest request = new ExportScribbleRequest(bitmap, page);
                readerDataHolder.submitNonRenderRequest(request, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        if (bitmap != null) {
                            bitmap.recycle();
                        }
                        if (requestPages.size() == 0) {
                            hideLoadingDialog();
                            if (baseCallback != null) {
                                baseCallback.done(request, e);
                            }
                        }
                    }
                });
            }
        });
    }
}
