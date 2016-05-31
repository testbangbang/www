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
import com.onyx.kreader.utils.StringUtils;

/**
 * Created by zhuzeng on 10/4/15.
 * TODO: add document features when opened.
 */
public class OpenRequest extends BaseReaderRequest {

    private String documentPath;
    private ReaderDocumentOptions srcOptions;

    public OpenRequest(final String path, final ReaderDocumentOptions documentOptions) {
        super();
        documentPath = path;
        srcOptions = documentOptions;
    }

    public void execute(final Reader reader) throws Exception {
        final BaseOptions options = DocumentOptionsProvider.loadDocumentOptions(getContext(), documentPath);
        final ReaderDocumentOptionsImpl documentOptions = options.documentOptions();
        final ReaderPluginOptions pluginOptions = options.pluginOptions();
        if (srcOptions != null && documentOptions != null) {
            if (StringUtils.isNotBlank(srcOptions.getDocumentPassword())) {
                documentOptions.setDocumentPassword(srcOptions.getDocumentPassword());
            }
            if (StringUtils.isNotBlank(srcOptions.getCompressedPassword())) {
                documentOptions.setCompressedPassword(srcOptions.getCompressedPassword());
            }
        }

        if (!reader.getReaderHelper().selectPlugin(getContext(), documentPath, pluginOptions)) {
            return;
        }

        try {
            ReaderDocument document = reader.getPlugin().open(documentPath, documentOptions, pluginOptions);
            reader.getReaderHelper().initData(getContext());
            reader.getReaderHelper().onDocumentOpened(document, documentOptions);
        } catch (ReaderException readerException) {
            throw readerException;
        }
    }

}
