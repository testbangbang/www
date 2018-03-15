package com.onyx.jdread.reader.request;

import com.onyx.android.sdk.reader.api.ReaderCallback;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.event.DocumentLoadSuccessEvent;
import com.onyx.jdread.reader.exception.FileFormatErrorException;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/20.
 */

public class OpenDocumentRequest extends ReaderBaseRequest {
    private ReaderPluginOptions pluginOptions;
    private BaseOptions srcOptions;
    private EventBus eventBus;

    public OpenDocumentRequest(Reader reader, BaseOptions baseOptions, EventBus eventBus) {
        super(reader);
        this.srcOptions = baseOptions;
        this.eventBus = eventBus;
    }

    @Override
    public void setAbort(boolean abort) {
        super.setAbort(abort);
        getReader().getReaderHelper().getPlugin().abortBookLoadingJob();
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
        final Reader reader = getReader();
        reader.getReaderHelper().getPlugin().setReaderCallback(new ReaderCallback() {
            @Override
            public void onDocumentLoadSuccess() {
                reader.getReaderHelper().setLoadComplete(true);
                if (eventBus != null) {
                    eventBus.post(new DocumentLoadSuccessEvent());
                }
            }
        });

        ReaderDocument document = getReader().getReaderHelper().openDocument(getReader().getDocumentInfo().getBookPath(), srcOptions, pluginOptions);
        if(document != null) {
            getReader().getReaderHelper().saveReaderDocument(document,getReader().getDocumentInfo());
            return true;
        }
        return false;
    }

    private boolean selectPlugin() {
        if (!getReader().getReaderHelper().selectPlugin(getAppContext(), getReader().getDocumentInfo(), pluginOptions)) {
            return false;
        }
        return true;
    }
}
