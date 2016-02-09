package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.navigation.NavigationArgs;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/17/15.
 */
public class SubScreenNavigationRequest extends BaseRequest {

    private NavigationArgs navigationArgs;

    public SubScreenNavigationRequest(final NavigationArgs args) {
        navigationArgs = args;
    }

    // check page at first. and then goto the location.
    public void execute(final Reader reader) throws Exception {
        useRenderBitmap(reader);
        reader.getReaderLayoutManager().setNavigationArgs(navigationArgs);
        reader.getReaderLayoutManager().drawVisiblePages(getRenderBitmap());
    }

}
