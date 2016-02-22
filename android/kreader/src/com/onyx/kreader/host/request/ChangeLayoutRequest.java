package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ChangeLayoutRequest extends BaseRequest {

    private String newLayout;
    private NavigationArgs navigationArgs;

    public ChangeLayoutRequest(final String layout, final NavigationArgs args) {
        newLayout = layout;
        navigationArgs = args;
    }

    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setCurrentLayout(newLayout);
        reader.getReaderLayoutManager().setNavigationArgs(navigationArgs);
        reader.getReaderLayoutManager().drawVisiblePages(reader, getRenderBitmap());
    }
}
