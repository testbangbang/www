package com.onyx.kreader.host.request;

import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.wrapper.Reader;

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
        reader.getDocument().updateDocumentOptions(reader.getDocumentOptions().documentOptions(),
                reader.getPluginOptions());
        drawVisiblePages(reader);
    }
}
