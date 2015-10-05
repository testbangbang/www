package com.onyx.reader.host.request;

import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class OpenRequest extends BaseRequest {

    private String documentPath;

    public OpenRequest(final String path) {
        super();
        documentPath = path;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getHelper().document = reader.getPlugin().open(documentPath, null, null);
    }



}
