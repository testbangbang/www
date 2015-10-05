package com.onyx.reader.host.request;

import com.onyx.reader.api.ReaderDocument;
import com.onyx.reader.api.ReaderDocumentOptions;
import com.onyx.reader.api.ReaderPluginOptions;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class OpenRequest extends BaseRequest {

    private String documentPath;
    private ReaderDocumentOptions documentOptions;
    private ReaderPluginOptions pluginOptions;

    public OpenRequest(final String path, final ReaderDocumentOptions doc, final ReaderPluginOptions plugin) {
        super();
        documentPath = path;
        documentOptions = doc;
        pluginOptions = plugin;
    }

    public void execute(final Reader reader) throws Exception {
        ReaderDocument document = reader.getReaderHelper().getCurrentPlugin().open(documentPath, documentOptions, pluginOptions);
        if (document != null) {
            reader.getReaderHelper().onDocumentOpened(document);
        }
    }



}
