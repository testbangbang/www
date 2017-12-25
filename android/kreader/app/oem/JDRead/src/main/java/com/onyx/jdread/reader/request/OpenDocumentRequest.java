package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.exception.FileFormatErrorException;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class OpenDocumentRequest extends RxRequest {
    private Reader reader;
    private ReaderDocumentOptionsImpl documentOptions;
    private ReaderPluginOptions pluginOptions;

    public OpenDocumentRequest(Reader reader) {
        this.reader = reader;
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
        ReaderDocument document = reader.getReaderHelper().getPlugin().open(reader.getDocumentInfo().getBookPath(), documentOptions, pluginOptions);
        if(document != null) {
            reader.getReaderHelper().saveReaderDocument(document);
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
