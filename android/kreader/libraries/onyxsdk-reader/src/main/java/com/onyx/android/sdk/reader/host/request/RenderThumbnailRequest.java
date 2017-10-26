package com.onyx.android.sdk.reader.host.request;

import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.reader.cache.ReaderBitmapReferenceImpl;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by ming on 2017/2/28.
 */

public class RenderThumbnailRequest extends BaseReaderRequest {

    private String page;
    private ReaderBitmap bitmap;
    private PageInfo pageInfo;
    private String pagePosition;
    private boolean renderByPage = false;

    public RenderThumbnailRequest(final String p, final String pagePosition, final ReaderBitmap bmp) {
        super();
        page = p;
        bitmap = bmp;
        this.pagePosition = pagePosition;
        setAbortPendingTasks(true);
    }

    public RenderThumbnailRequest(final String p, final String pagePosition, final ReaderBitmap bmp, final boolean renderByPage) {
        this(p, pagePosition, bmp);
        this.renderByPage = renderByPage;
    }

    public static RenderThumbnailRequest renderByPage(final String p, final String pagePosition, final ReaderBitmap bmp) {
        return new RenderThumbnailRequest(p, pagePosition, bmp, true);
    }

    public static RenderThumbnailRequest renderByPage(final String p, final String pagePosition, final ReaderBitmap bmp, boolean abortPendingTasks) {
        RenderThumbnailRequest request = renderByPage(p, pagePosition, bmp);
        request.setAbortPendingTasks(abortPendingTasks);
        return request;
    }

    public static RenderThumbnailRequest renderByPosition(final String p, final String pagePosition, final ReaderBitmap bmp) {
        return new RenderThumbnailRequest(p, pagePosition, bmp, false);
    }

    @Override
    public void execute(Reader reader) throws Exception {
        final RectF origin = reader.getDocument().getPageOriginSize(page);

        if (renderByPage) {
            reader.getNavigator().gotoPage(PagePositionUtils.getPageNumber(page));
        }else if (!StringUtils.isNullOrEmpty(pagePosition)){
            reader.getNavigator().gotoPosition(pagePosition);
        }

        String startPosition = reader.getNavigator().getScreenStartPosition();
        if (StringUtils.isNullOrEmpty(startPosition)) {
            startPosition = page;
        }
        String endPosition = reader.getNavigator().getScreenEndPosition();
        if (StringUtils.isNullOrEmpty(endPosition)) {
            endPosition = page;
        }
        PageInfo pageInfo = new PageInfo(page, startPosition, endPosition, origin.width(), origin.height());
        if (reader.getRendererFeatures().supportScale()) {
            this.pageInfo = LayoutProviderUtils.drawPageWithScaleToPage(pageInfo, bitmap, reader.getRenderer());
        } else {
            ReaderBitmapReferenceImpl originalPage = ReaderBitmapReferenceImpl.create((int)origin.width(), (int)origin.height(), ReaderBitmapReferenceImpl.DEFAULT_CONFIG);
            this.pageInfo = LayoutProviderUtils.drawReflowablePage(pageInfo, originalPage, reader.getRenderer());
            if (this.pageInfo != null) {
                BitmapUtils.scaleBitmap(originalPage.getBitmap(),
                        new Rect(0, 0, (int)origin.width(), (int)origin.height()),
                        bitmap.getBitmap(),
                        new Rect(0, 0, bitmap.getBitmap().getWidth(), bitmap.getBitmap().getHeight()));
            }
            originalPage.close();
        }
    }

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
