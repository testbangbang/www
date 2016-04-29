package com.onyx.kreader.host.request;

import android.graphics.RectF;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.math.PageInfo;
import com.onyx.kreader.host.math.PageUtils;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ScaleByRectRequest extends BaseRequest  {

    private RectF childInDocument;
    private String pageName;

    public ScaleByRectRequest(final String name, final RectF c) {
        pageName = name;
        childInDocument = c;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().scaleByRect(pageName, childInDocument);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }

    public static RectF rectInDocument(final PageInfo pageInfo, final RectF rectInScreen) {
        PageUtils.translateCoordinates(rectInScreen, pageInfo.getDisplayRect());
        rectInScreen.offset(pageInfo.getPositionRect().left, pageInfo.getPositionRect().top);
        return rectInScreen;
    }
}
