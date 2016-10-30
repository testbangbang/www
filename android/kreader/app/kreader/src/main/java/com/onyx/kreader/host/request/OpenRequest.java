package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class OpenRequest extends BaseReaderRequest {

    private String documentPath;
    private BaseOptions srcOptions;

    public OpenRequest(final String path, final BaseOptions documentOptions) {
        super();
        documentPath = path;
        srcOptions = documentOptions;
    }

    public void execute(final Reader reader) throws Exception {
        final ReaderDocumentOptionsImpl documentOptions = srcOptions.documentOptions();
        ReaderPluginOptions pluginOptions = srcOptions.pluginOptions();
        if (pluginOptions == null) {
            pluginOptions = ReaderPluginOptionsImpl.create(getContext());
        }

        if (!reader.getReaderHelper().selectPlugin(getContext(), documentPath, pluginOptions)) {
            return;
        }

        try {
            ReaderDocument document = reader.getPlugin().open(documentPath, documentOptions, pluginOptions);
            reader.getReaderHelper().onDocumentOpened(getContext(), documentPath, document, srcOptions);
            // we should init data after document is opened
            reader.getReaderHelper().initData(getContext());
        } catch (ReaderException readerException) {
            throw readerException;
        }
    }

}
