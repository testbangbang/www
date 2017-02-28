package com.onyx.android.sdk.reader.host.request;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.reader.cache.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.utils.PagePositionUtils;
import com.onyx.android.sdk.utils.StringUtils;

/**
 * Created by zengzhu on 3/11/16.
 * Render thumbnail without using layout manager.
 */
public class RenderThumbnailRequest extends BaseReaderRequest {

    private String page;
    private ReaderBitmap bitmap;
    private PageInfo pageInfo;
    private boolean nextPage = false;

    public RenderThumbnailRequest(final String p, final ReaderBitmap bmp) {
        super();
        page = p;
        bitmap = bmp;
        setAbortPendingTasks(true);
    }

    public RenderThumbnailRequest(final String p, final ReaderBitmap bmp, final boolean nextPage) {
        super();
        page = p;
        bitmap = bmp;
        this.nextPage = nextPage;
        setAbortPendingTasks(true);
    }

    public static RenderThumbnailRequest nextPageThumbnailRequest(final String p, final ReaderBitmap bmp) {
        return new RenderThumbnailRequest(p, bmp, true);
    }

    public static RenderThumbnailRequest pageThumbnailRequest(final String p, final ReaderBitmap bmp) {
        return new RenderThumbnailRequest(p, bmp);
    }

    public void execute(final Reader reader) throws Exception {
        final RectF origin = reader.getDocument().getPageOriginSize(page);

        String pagePosition = reader.getNavigator().getPositionByPageNumber(PagePositionUtils.getPageNumber(page));
        if (isNextPage()) {
            reader.getNavigator().nextScreen(pagePosition);
        }else {
            reader.getNavigator().gotoPosition(pagePosition);
        }

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

    public boolean isNextPage() {
        return nextPage;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }
}
