package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class ChangeViewConfigRequest extends BaseReaderRequest {

    private int newWidth, newHeight;
    private String position;

    public ChangeViewConfigRequest(int nw, int nh, final String position) {
        newWidth = nw;
        newHeight = nh;
        this.position = position;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);

        drawVisiblePages(reader);
    }
}
