package com.onyx.kreader.host.request;

import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderDocumentOptions;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.dataprovider.DocumentOptionsProvider;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
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
        setSaveOptions(false);
        final ReaderDocumentOptionsImpl documentOptions = srcOptions.documentOptions();
        final ReaderPluginOptions pluginOptions = srcOptions.pluginOptions();

        if (!reader.getReaderHelper().selectPlugin(getContext(), documentPath, pluginOptions)) {
            return;
        }

        try {
            ReaderDocument document = reader.getPlugin().open(documentPath, documentOptions, pluginOptions);
            reader.getReaderHelper().initData(getContext());
            reader.getReaderHelper().onDocumentOpened(getContext(), documentPath, document, srcOptions);
        } catch (ReaderException readerException) {
            throw readerException;
        }
    }

}
