package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/5/15.
 * redraw request
 */
public class UpdateScreenOrientationOptionRequest extends BaseReaderRequest {

    private int orientation;

    public UpdateScreenOrientationOptionRequest(int orientation) {
        this.orientation = orientation;
    }

    public void execute(final Reader reader) throws Exception {
        setSaveOptions(true);
        reader.getDocumentOptions().setOrientation(orientation);
    }

}
