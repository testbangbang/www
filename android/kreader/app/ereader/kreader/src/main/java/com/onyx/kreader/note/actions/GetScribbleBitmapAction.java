package com.onyx.kreader.note.actions;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.telecom.Call;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.host.math.PageUtils;
import com.onyx.android.sdk.reader.host.request.RenderThumbnailRequest;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.ReaderNoteRenderRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;
import com.onyx.kreader.ui.dialog.DialogTabHostMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/9/23.
 */
public class GetScribbleBitmapAction {

    public interface Callback{
        void onNext(PageInfo page, Bitmap bitmap, PageInfo pageInfo);
    }

    private List<PageInfo> requestPages;
    private int width;
    private int height;

    private Bitmap scribbleBitmap;
    private ReaderBitmapImpl contentBitmap;
    private Rect size;

    public GetScribbleBitmapAction(List<PageInfo> pages, int width, int height) {
        this.requestPages = pages;
        this.width = width;
        this.height = height;
        size = new Rect(0, 0, width, height);
    }

    public void execute(final ReaderDataHolder readerDataHolder, final Callback callback) {
        requestPreviewBySequence(readerDataHolder,callback);
    }

    private void drawScribbleBitmap(Bitmap originBitmap){
        Canvas canvas = new Canvas(originBitmap);
        Paint myPainter = new Paint();
        canvas.drawBitmap(scribbleBitmap, 0, 0, myPainter);
    }

    public ReaderBitmapImpl getContentBitmap() {
        return contentBitmap;
    }

    private void requestPreviewBySequence(final ReaderDataHolder readerDataHolder, final Callback callback) {
        if (requestPages.size() <= 0){
            return;
        }

        final PageInfo currentPage = requestPages.remove(0);
        contentBitmap = ReaderBitmapImpl.create(width, height, Bitmap.Config.ARGB_8888);
        if (readerDataHolder.supportScalable() && currentPage.getSubPage() == 0) {
            final RenderThumbnailRequest thumbnailRequest = RenderThumbnailRequest.renderByPage(currentPage.getName(), null, contentBitmap);
            readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), thumbnailRequest, new BaseCallback() {

                @Override
                public void done(BaseRequest request, Throwable e) {
                    drawScribblePage(readerDataHolder, currentPage, callback);
                }
            });
        } else {
            drawScribblePage(readerDataHolder, currentPage, callback);
        }
    }

    private void drawScribblePage(final ReaderDataHolder readerDataHolder, final PageInfo currentPage, final Callback callback) {
        final NoteManager noteManager = readerDataHolder.getNoteManager();
        List<PageInfo> pageInfoList = new ArrayList<>();
        final RectF origin = readerDataHolder.getReader().getDocument().getPageOriginSize(currentPage.getName());

        final PageInfo pageInfo = new PageInfo(currentPage.getName(), currentPage.getSubPage(),
                origin.width(), origin.height());
        float scale = PageUtils.scaleToPage(origin.width(), origin.height(), width, height);
        pageInfo.setScale(scale);
        pageInfo.updateDisplayRect(pageInfo.getDisplayRect());

        pageInfoList.add(pageInfo);
        final ReaderNoteRenderRequest noteRequest = new ReaderNoteRenderRequest(
                readerDataHolder.getReader().getDocumentMd5(),
                pageInfoList,
                size,
                false);

        noteManager.submit(readerDataHolder.getContext(), noteRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                scribbleBitmap = noteManager.getViewBitmap();
                drawScribbleBitmap(contentBitmap.getBitmap());
                callback.onNext(currentPage, contentBitmap.getBitmap(), pageInfo);
                requestPreviewBySequence(readerDataHolder, callback);
            }
        });
    }
}
