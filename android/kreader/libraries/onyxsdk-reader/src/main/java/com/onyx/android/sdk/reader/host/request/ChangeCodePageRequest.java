package com.onyx.android.sdk.reader.host.request;

import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 5/27/16.
 */
public class ChangeCodePageRequest extends BaseReaderRequest {

    private int codePage;

    public ChangeCodePageRequest(int codePage) {
        this.codePage = codePage;
    }

    public void execute(final Reader reader) throws Exception {
        reader.getDocumentOptions().setCodePage(codePage);
        saveReaderOptions(reader);
        reader.getBitmapCache().clear();
        reader.getDocument().updateDocumentOptions(reader.getDocumentOptions().documentOptions(),
                reader.getPluginOptions());
        drawVisiblePages(reader);
    }
}
