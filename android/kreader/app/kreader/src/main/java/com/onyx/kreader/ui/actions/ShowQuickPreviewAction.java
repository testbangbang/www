package com.onyx.kreader.ui.actions;

import android.graphics.Bitmap;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.kreader.host.request.RenderThumbnailRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogQuickPreview;
import com.onyx.kreader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 7/15/16.
 */
public class ShowQuickPreviewAction extends BaseAction {

    private static final String TAG = "ShowQuickPreviewAction";

    private DialogQuickPreview dialogQuickPreview;
    private List<Integer> pagesToPreview;
    private List<ReaderBitmapImpl> bitmaps = new ArrayList<>();
    private int index = 0;
    private int width = 300;
    private int height = 400;

    public ShowQuickPreviewAction(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.isFixedDocument()) {
            width = readerDataHolder.getDisplayWidth();
            height = readerDataHolder.getDisplayHeight();
        }
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {
        dialogQuickPreview = new DialogQuickPreview(readerDataHolder, new DialogQuickPreview.Callback() {

            @Override
            public void abort() {
                if (pagesToPreview != null) {
                    index = 0;
                    pagesToPreview.clear();
                }
            }

            @Override
            public void requestPreview(final List<Integer> pages) {
                pagesToPreview = pages;
                index = 0;
                requestPreviewBySequence(readerDataHolder);
            }

            @Override
            public void recycleBitmap() {
                for (ReaderBitmapImpl bitmap : bitmaps) {
                    bitmap.recycleBitmap();
                }
            }
        });
        dialogQuickPreview.show();
        readerDataHolder.addActiveDialog(dialogQuickPreview);
        BaseCallback.invoke(callback, null, null);
    }

    private void requestPreviewBySequence(final ReaderDataHolder readerDataHolder) {
        if (pagesToPreview.size() <= 0) {
            return;
        }
        final int current = pagesToPreview.remove(0);
        final ReaderBitmapImpl readerBitmap;
        if (index >= bitmaps.size()) {
            readerBitmap = new ReaderBitmapImpl(width, height, Bitmap.Config.ARGB_8888);
            bitmaps.add(readerBitmap);
        }else {
            readerBitmap = bitmaps.get(index);
        }
        index++;
        RenderThumbnailRequest thumbnailRequest = new RenderThumbnailRequest(PagePositionUtils.fromPageNumber(current), readerBitmap);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), thumbnailRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dialogQuickPreview.updatePreview(current, readerBitmap.getBitmap());
                requestPreviewBySequence(readerDataHolder);
            }
        });
    }
}
