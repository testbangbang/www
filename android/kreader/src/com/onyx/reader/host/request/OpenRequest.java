package com.onyx.reader.host.request;

import android.content.Context;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.plugin.ReaderDocument;

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
        reader.getReaderHelper().document = reader.getPlugin().open(documentPath, null, null);
    }



}
