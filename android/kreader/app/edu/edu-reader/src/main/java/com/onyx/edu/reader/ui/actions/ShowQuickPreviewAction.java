package com.onyx.edu.reader.ui.actions;

import android.graphics.Bitmap;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.host.request.NextScreenRequest;
import com.onyx.android.sdk.reader.host.request.RenderThumbnailRequest;
import com.onyx.edu.reader.ui.data.ReaderDataHolder;
import com.onyx.edu.reader.ui.dialog.DialogQuickPreview;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy on 7/15/16.
 */
public class ShowQuickPreviewAction extends BaseAction {

    private static final String TAG = "ShowQuickPreviewAction";

    private DialogQuickPreview dialogQuickPreview;
    private List<Integer> pagesToPreview;
    private List<String> positionsToPreview;
    private List<ReaderBitmapImpl> bitmaps = new ArrayList<>();
    private int index = 0;
    private int width = 300;
    private int height = 400;

    public ShowQuickPreviewAction(ReaderDataHolder readerDataHolder) {
        if (readerDataHolder.isFixedPageDocument()) {
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
            public void requestPreview(final List<String> positions, final List<Integer> pages) {
                pagesToPreview = pages;
                positionsToPreview = positions;
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
        BaseCallback.invoke(callback, null, null);
    }

    private void requestPreviewBySequence(final ReaderDataHolder readerDataHolder) {
        if (pagesToPreview.size() <= 0 || positionsToPreview.size() <= 0) {
            return;
        }
        final int current = pagesToPreview.remove(0);
        final String position = positionsToPreview.remove(0);
        final ReaderBitmapImpl readerBitmap;
        if (index >= bitmaps.size()) {
            readerBitmap = new ReaderBitmapImpl(width, height, Bitmap.Config.ARGB_8888);
            bitmaps.add(readerBitmap);
        }else {
            readerBitmap = bitmaps.get(index);
        }
        final String pageName = PagePositionUtils.fromPageNumber(current);
        if (index > 0) {
            final NextScreenRequest screenRequest = new NextScreenRequest(true);
            readerDataHolder.submitNonRenderRequest(screenRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    if (screenRequest.isAbort()) {
                        return;
                    }
                    index++;
                    renderThumbnailRequest(readerDataHolder, readerBitmap, pageName, null);
                }
            });
        }else {
            index++;
            renderThumbnailRequest(readerDataHolder, readerBitmap, pageName, position);
        }
    }

    private void renderThumbnailRequest(final ReaderDataHolder readerDataHolder, final ReaderBitmapImpl readerBitmap, final String pageName, final String position) {
        final RenderThumbnailRequest thumbnailRequest = RenderThumbnailRequest.renderByPosition(pageName, position, readerBitmap);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), thumbnailRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (thumbnailRequest.isAbort()) {
                    return;
                }
                dialogQuickPreview.updatePreview(thumbnailRequest.getPageInfo(), readerBitmap.getBitmap());
                requestPreviewBySequence(readerDataHolder);
            }
        });
    }
}
