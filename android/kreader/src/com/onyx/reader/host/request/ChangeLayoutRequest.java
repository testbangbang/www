package com.onyx.reader.host.request;

import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/15/15.
 */
public class ChangeLayoutRequest extends BaseRequest {

    private String newLayout;

    public ChangeLayoutRequest(final String layout) {
        newLayout = layout;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderLayoutManager().setCurrentLayout(newLayout);
    }
}
