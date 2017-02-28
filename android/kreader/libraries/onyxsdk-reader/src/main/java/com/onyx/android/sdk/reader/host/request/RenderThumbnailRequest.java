package com.onyx.android.sdk.reader.host.request;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.cache.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.utils.BitmapUtils;

/**
 * Created by ming on 2017/2/28.
 */

public abstract class RenderThumbnailRequest extends BaseReaderRequest {

    private String page;
    private ReaderBitmap bitmap;
    private PageInfo pageInfo;

    public RenderThumbnailRequest(final String p, final ReaderBitmap bmp) {
        super();
        page = p;
        bitmap = bmp;
        setAbortPendingTasks(true);
    }

    @Override
    public void execute(Reader reader) throws Exception {
        final RectF origin = reader.getDocument().getPageOriginSize(page);

        locationThumbnailRange(reader);

        String position = reader.getNavigator().getScreenStartPosition();
        PageInfo pageInfo = new PageInfo(page, position, origin.width(), origin.height());
        if (reader.getRendererFeatures().supportScale()) {
            this.pageInfo = LayoutProviderUtils.drawPageWithScaleToPage(pageInfo, bitmap, reader.getRenderer());
        } else {
            ReaderBitmap originalPage = ReaderBitmapImpl.create((int)origin.width(), (int)origin.height(), Bitmap.Config.ARGB_8888);
            this.pageInfo = LayoutProviderUtils.drawReflowablePage(pageInfo, originalPage, reader.getRenderer());
            if (this.pageInfo != null) {
                BitmapUtils.scaleBitmap(originalPage.getBitmap(),
                        new Rect(0, 0, (int)origin.width(), (int)origin.height()),
                        bitmap.getBitmap(),
                        new Rect(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight()));
            }
            originalPage.getBitmap().recycle();
        }
    }

    protected abstract void locationThumbnailRange(final Reader reader);

    public String getPage() {
        return page;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public ReaderBitmap getBitmap() {
        return bitmap;
    }
}
