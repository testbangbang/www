package com.onyx.jdread.reader.request;

import android.graphics.Bitmap;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.data.utils.ThumbnailUtils;
import com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.plugins.alreader.AlReaderWrapper;
import com.onyx.android.sdk.reader.utils.ImageUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.jdread.reader.data.Reader;


/**
 * Created by huxiaomao on 2018/1/25.
 */

public class ReaderEpubCoverRequest extends ReaderBaseRequest {
    private ReaderBitmap cover;
    private int width;
    private int height;

    public ReaderEpubCoverRequest(Reader reader, int width, int height) {
        super(reader);
        this.width = width;
        this.height = height;
    }

    @Override
    public ReaderEpubCoverRequest call() throws Exception {
        String documentPath = getReader().getDocumentInfo().getBookPath();

        AlReaderWrapper wrapper = new AlReaderWrapper(getAppContext(), ReaderPluginOptionsImpl.create(getAppContext()));
        Bitmap bmp = wrapper.scanCover(documentPath);
        if (bmp == null) {
            return this;
        }

        try {
            cover = ReaderBitmapImpl.create(width, height, Bitmap.Config.ARGB_8888);
            //BitmapUtils.scaleToFitCenter(bmp, cover.getBitmap());
            ImageUtils.applyGammaCorrection(cover.getBitmap(), 150.0f, null);
            getReader().getReaderHelper().setFileMd5(getReader().getDocumentInfo());
            ThumbnailUtils.insertThumbnail(getAppContext(), ContentSdkDataUtils.getDataProvider(), documentPath, getReader().getReaderHelper().getDocumentMd5(), cover.getBitmap());
            return this;
        } finally {
            bmp.recycle();
        }
    }

    public ReaderBitmap getCover() {
        return cover;
    }
}
