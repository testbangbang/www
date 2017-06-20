package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 5/29/16.
 */
public class ScaleToPageCropRequest extends BaseReaderRequest {

    private String pageName;

    public ScaleToPageCropRequest(final String name) {
        pageName = name;
    }

    // in document coordinates system. forward to layout manager to scale
    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().setCurrentLayout(PageConstants.SINGLE_PAGE, new NavigationArgs());
        reader.getReaderLayoutManager().scaleToPageContent(pageName);
        drawVisiblePages(reader);
    }
}
