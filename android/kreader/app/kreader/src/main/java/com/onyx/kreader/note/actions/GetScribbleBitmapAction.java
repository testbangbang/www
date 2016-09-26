package com.onyx.kreader.note.actions;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.kreader.host.request.RenderThumbnailRequest;
import com.onyx.kreader.note.NoteManager;
import com.onyx.kreader.note.request.ReaderNoteRenderRequest;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/9/23.
 */
public class GetScribbleBitmapAction{

    public interface Callback{
        void onNext(String page, Bitmap bitmap);
    }

    private List<String> requestPages;
    private int width;
    private int height;

    private Bitmap scribbleBitmap;
    private ReaderBitmapImpl contentBitmap;
    private Rect size;
    private boolean isAborted = false;

    public GetScribbleBitmapAction(List<String> page, int width, int height) {
        this.requestPages = page;
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
        if (isAborted || requestPages.size() <= 0){
            return;
        }

        final String currentPage = requestPages.remove(0);
        contentBitmap = ReaderBitmapImpl.create(width, height, Bitmap.Config.ARGB_8888);
        final RenderThumbnailRequest thumbnailRequest = new RenderThumbnailRequest(currentPage, contentBitmap);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), thumbnailRequest, new BaseCallback() {

            @Override
            public void done(BaseRequest request, Throwable e) {
                final NoteManager noteManager = readerDataHolder.getNoteManager();
                List<PageInfo> pageInfoList = new ArrayList<>();
                pageInfoList.add(thumbnailRequest.getPageInfo());
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
                        callback.onNext(currentPage, contentBitmap.getBitmap());
                        requestPreviewBySequence(readerDataHolder, callback);
                    }
                });
            }
        });
    }

    public void setAborted(boolean aborted) {
        isAborted = aborted;
    }
}
