package com.onyx.reader.host.request;

import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.navigation.NavigationArgs;
import com.onyx.reader.host.wrapper.Reader;

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
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }
}
