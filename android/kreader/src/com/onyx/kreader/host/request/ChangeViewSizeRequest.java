package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/6/15.
 */
public class ChangeViewSizeRequest extends BaseRequest {

    private int newWidth, newHeight;

    public ChangeViewSizeRequest(int nw, int nh) {
        newWidth = nw;
        newHeight = nh;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderHelper().updateRenderBitmap(newWidth, newHeight);
    }

}
