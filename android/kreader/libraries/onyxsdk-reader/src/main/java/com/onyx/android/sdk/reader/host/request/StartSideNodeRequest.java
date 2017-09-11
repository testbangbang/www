package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class StartSideNodeRequest extends BaseReaderRequest {

    private int newWidth, newHeight;

    public StartSideNodeRequest(int nw, int nh) {
        newWidth = nw / 2;
        newHeight = nh;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);

        drawVisiblePages(reader);
    }
}
