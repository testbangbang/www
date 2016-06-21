package com.onyx.kreader.host.request;

import android.graphics.RectF;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 3/11/16.
 * Render thumbnail without using layout manager.
 */
public class RenderThumbnailRequest extends BaseReaderRequest {

    private String page;
    private ReaderBitmap bitmap;

    public RenderThumbnailRequest(final String p, final ReaderBitmap bmp) {
        super();
        page = p;
        bitmap = bmp;
    }

    public void execute(final Reader reader) throws Exception {
        final RectF origin = reader.getDocument().getPageOriginSize(page);
        PageInfo pageInfo = new PageInfo(page, origin.width(), origin.height());
        LayoutProviderUtils.drawPageWithScaleToPage(pageInfo, bitmap, reader.getRenderer());
    }
}
