package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class ChangeViewConfigRequest extends BaseRequest {

    private int newWidth, newHeight;

    public ChangeViewConfigRequest(int nw, int nh) {
        newWidth = nw;
        newHeight = nh;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getReaderHelper().updateRenderBitmap(newWidth, newHeight);
        reader.getReaderCacheManager().clear();
    }
}
