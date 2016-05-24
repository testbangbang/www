package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class ChangeViewConfigRequest extends BaseRequest {

    private int newWidth, newHeight;
    private String position;

    public ChangeViewConfigRequest(int nw, int nh, final String position) {
        newWidth = nw;
        newHeight = nh;
        this.position = position;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderHelper().updateViewportSize(newWidth, newHeight);
        reader.getReaderLayoutManager().gotoPosition(position);
    }
}
