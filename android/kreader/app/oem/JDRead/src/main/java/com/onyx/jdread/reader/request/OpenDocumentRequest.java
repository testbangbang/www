package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.exception.FileFormatErrorException;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class OpenDocumentRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderDocumentOptionsImpl documentOptions;
    private ReaderPluginOptions pluginOptions;

    public OpenDocumentRequest(ReaderDataHolder readerDataHolder) {
        this.readerDataHolder = readerDataHolder;
    }

    @Override
    public OpenDocumentRequest call() throws Exception {
        initOptions();
        if (!selectPlugin()) {
            throw new FileFormatErrorException(getAppContext().getString(R.string.file_format_error));
        }
        openDocument();
        return this;
    }

    public void initOptions() {
        documentOptions = new ReaderDocumentOptionsImpl(null, null);
        pluginOptions = ReaderPluginOptionsImpl.create(getAppContext());
    }

    private boolean openDocument() throws Exception {
        ReaderDocument document = readerDataHolder.getReader().getReaderHelper().openDocument(readerDataHolder.getReader().getDocumentInfo().getBookPath(), documentOptions, pluginOptions);
        if(document != null) {
            readerDataHolder.getReader().getReaderHelper().saveReaderDocument(document);
            return true;
        }
        return false;
    }

    private boolean selectPlugin() {
        if (!readerDataHolder.getReader().getReaderHelper().selectPlugin(getAppContext(), readerDataHolder.getReader().getDocumentInfo(), pluginOptions)) {
            return false;
        }
        return true;
    }
}
