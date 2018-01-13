package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.exception.FileFormatErrorException;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class OpenDocumentRequest extends ReaderBaseRequest {
    private ReaderDataHolder readerDataHolder;
    private ReaderPluginOptions pluginOptions;
    private BaseOptions srcOptions;

    public OpenDocumentRequest(ReaderDataHolder readerDataHolder,BaseOptions baseOptions) {
        this.readerDataHolder = readerDataHolder;
        this.srcOptions = baseOptions;
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
        pluginOptions = srcOptions.pluginOptions();
        if (pluginOptions == null) {
            pluginOptions = ReaderPluginOptionsImpl.create(getAppContext());
        }

    }

    private boolean openDocument() throws Exception {
        ReaderDocument document = readerDataHolder.openDocument(readerDataHolder.getReader().getDocumentInfo().getBookPath(), srcOptions, pluginOptions);
        if(document != null) {
            readerDataHolder.saveReaderDocument(document,readerDataHolder.getReader().getDocumentInfo());
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
