package com.onyx.kreader.host.request;

import android.graphics.RectF;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.android.sdk.data.PageInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleByRectRequest extends BaseReaderRequest {

    private RectF childInDocument;
    private String pageName;

    public ScaleByRectRequest(final String name, final RectF c) {
        pageName = name;
        childInDocument = c;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().scaleByRect(pageName, childInDocument);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }

    public static RectF rectInDocument(final PageInfo pageInfo, final RectF rectInScreen) {
        PageUtils.translateCoordinates(rectInScreen, pageInfo.getDisplayRect());
        rectInScreen.offset(pageInfo.getPositionRect().left, pageInfo.getPositionRect().top);
        return rectInScreen;
    }
}
