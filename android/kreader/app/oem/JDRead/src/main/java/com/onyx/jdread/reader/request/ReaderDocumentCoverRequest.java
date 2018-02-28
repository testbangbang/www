package com.onyx.jdread.reader.request;

import android.graphics.Bitmap;
import android.util.Log;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.jdread.reader.data.Reader;

/**
 * Created by huxiaomao on 2018/1/25.
 */

public class ReaderDocumentCoverRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderBitmap cover;
    private int width;
    private int height;

    public ReaderDocumentCoverRequest(Reader reader, int width, int height) {
        this.reader = reader;
        this.width = width;
        this.height = height;
    }

    @Override
    public ReaderDocumentCoverRequest call() throws Exception {
        cover = ReaderBitmapImpl.create(width, height, Bitmap.Config.ARGB_8888);
        boolean readCover = reader.getReaderHelper().getDocument().readCover(cover.getBitmap());
        String documentPath = reader.getDocumentInfo().getBookPath();
        if (readCover) {
            ThumbnailUtils.insertThumbnail(getAppContext(), ContentSdkDataUtils.getDataProvider(), documentPath, reader.getReaderHelper().getDocumentMd5(), cover.getBitmap());
        }
        return this;
    }

    public ReaderBitmap getCover() {
        return cover;
    }
}
