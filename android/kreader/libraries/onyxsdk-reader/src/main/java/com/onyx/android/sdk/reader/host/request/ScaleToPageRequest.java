package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ScaleToPageRequest extends BaseReaderRequest {

    private String pageName;

    public ScaleToPageRequest(final String name) {
        pageName = name;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        reader.getReaderLayoutManager().scaleToPage(pageName);
        drawVisiblePages(reader);
    }

}
