package com.onyx.kreader.host.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 5/29/16.
 */
public class ScaleToWidthContentRequest extends BaseReaderRequest {

    private String pageName;

    public ScaleToWidthContentRequest(final String name) {
        pageName = name;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        reader.getReaderLayoutManager().scaleToWidthContent(pageName);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap(), createReaderViewInfo());
    }
}
