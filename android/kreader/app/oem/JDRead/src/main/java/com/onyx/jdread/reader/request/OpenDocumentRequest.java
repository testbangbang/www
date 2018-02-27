package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.exception.FileFormatErrorException;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class OpenDocumentRequest extends ReaderBaseRequest {
    private Reader reader;
    private ReaderPluginOptions pluginOptions;
    private BaseOptions srcOptions;

    public OpenDocumentRequest(Reader reader,BaseOptions baseOptions) {
        this.reader = reader;
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
            pluginOptions = ReaderPluginOptionsImpl.create(getAppContext()).setLoadAllImages(false);
        }

    }

    private boolean openDocument() throws Exception {
        ReaderDocument document = reader.getReaderHelper().openDocument(reader.getDocumentInfo().getBookPath(), srcOptions, pluginOptions);
        if(document != null) {
            reader.getReaderHelper().saveReaderDocument(document,reader.getDocumentInfo());
            return true;
        }
        return false;
    }

    private boolean selectPlugin() {
        if (!reader.getReaderHelper().selectPlugin(getAppContext(), reader.getDocumentInfo(), pluginOptions)) {
            return false;
        }
        return true;
    }
}
