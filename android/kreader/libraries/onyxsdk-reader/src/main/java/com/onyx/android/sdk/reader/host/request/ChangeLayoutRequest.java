package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ChangeLayoutRequest extends BaseReaderRequest {

    private String newLayout;
    private NavigationArgs navigationArgs;

    public ChangeLayoutRequest(final String layout, final NavigationArgs args) {
        newLayout = layout;
        navigationArgs = args;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderLayoutManager().setSavePosition(true);
        reader.getReaderLayoutManager().setCurrentLayout(newLayout, navigationArgs);
        drawVisiblePages(reader);
    }
}
