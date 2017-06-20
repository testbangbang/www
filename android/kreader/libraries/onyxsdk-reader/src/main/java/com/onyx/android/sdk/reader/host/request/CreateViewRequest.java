package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class CreateViewRequest extends BaseReaderRequest {

    private int newWidth, newHeight;

    public CreateViewRequest(int nw, int nh) {
        newWidth = nw;
        newHeight = nh;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);
    }

}
