package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseRequest;
import com.onyx.kreader.host.wrapper.Reader;

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
        if (!reader.getReaderHelper().selectPlugin(getContext(), documentPath, pluginOptions)) {
            return;
        }
        ReaderDocument document = reader.getPlugin().open(documentPath, documentOptions, pluginOptions);
        if (document != null) {
            reader.getReaderHelper().initData(getContext());
            reader.getReaderHelper().onDocumentOpened(document);
        }
    }

}
