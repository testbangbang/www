package com.onyx.kreader.host.request;

import android.os.Build;

import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.api.ReaderPluginOptions;
import com.onyx.kreader.common.BaseReaderRequest;
import com.onyx.kreader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.kreader.host.options.BaseOptions;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class OpenRequest extends BaseReaderRequest {

    private String documentPath;
    private BaseOptions srcOptions;
    private volatile boolean verifyDevice;

    public OpenRequest(final String path, final BaseOptions documentOptions, boolean verify) {
        super();
        documentPath = path;
        srcOptions = documentOptions;
        verifyDevice = verify;
    }

    public void execute(final Reader reader) throws Exception {
        if (!checkDevice()) {
            throw new ReaderException(ReaderException.ACTIVATION_FAILED, "");
        }

        final ReaderDocumentOptionsImpl documentOptions = srcOptions.documentOptions();
        final ReaderPluginOptions pluginOptions = srcOptions.pluginOptions();

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

    private boolean checkDevice() {
        if (!verifyDevice) {
            return true;
        }
        final String TAG = "onyx";
        if (Build.MANUFACTURER.toLowerCase().contains(TAG)) {
            return true;
        }
        if (Build.BRAND.toLowerCase().contains(TAG)) {
            return true;
        }
        if (Build.FINGERPRINT.toLowerCase().contains(TAG)) {
            return true;
        }
        return false;
    }

}
