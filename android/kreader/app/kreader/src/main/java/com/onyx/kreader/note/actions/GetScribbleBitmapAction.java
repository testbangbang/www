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
import com.onyx.kreader.ui.actions.BaseAction;
import com.onyx.kreader.ui.data.ReaderDataHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 16/9/23.
 */
public class GetScribbleBitmapAction extends BaseAction{

    private String page;
    private int width;
    private int height;

    private Bitmap scribbleBitmap;
    private ReaderBitmapImpl pdfBitmapImpl;
    private Rect size;

    public GetScribbleBitmapAction(String page, int width, int height) {
        this.page = page;
        this.width = width;
        this.height = height;
        size = new Rect(0, 0, width, height);
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final BaseCallback callback) {

        pdfBitmapImpl = new ReaderBitmapImpl(width, height, Bitmap.Config.ARGB_8888);
        final RenderThumbnailRequest thumbnailRequest = new RenderThumbnailRequest(page, pdfBitmapImpl);
        readerDataHolder.getReader().submitRequest(readerDataHolder.getContext(), thumbnailRequest, new BaseCallback() {

            @Override
            public void done(BaseRequest request, Throwable e) {
                final NoteManager noteManager = readerDataHolder.getNoteManager();
                List<PageInfo> pageInfos = new ArrayList<>();
                pageInfos.add(thumbnailRequest.getPageInfo());
                final ReaderNoteRenderRequest noteRequest = new ReaderNoteRenderRequest(page, pageInfos, size);

                noteManager.submit(readerDataHolder.getContext(), noteRequest, new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        scribbleBitmap = noteManager.getViewBitmap();
                        drawScribbleBitmap(pdfBitmapImpl.getBitmap());
                        callback.done(request, e);
                    }
                });
            }
        });
    }

    private void drawScribbleBitmap(Bitmap originBitmap){
        Canvas canvas = new Canvas(originBitmap);
        Paint myPainter = new Paint();
        canvas.drawBitmap(scribbleBitmap, 0, 0, myPainter);
    }

    public ReaderBitmapImpl getPdfBitmapImpl() {
        return pdfBitmapImpl;
    }
}
