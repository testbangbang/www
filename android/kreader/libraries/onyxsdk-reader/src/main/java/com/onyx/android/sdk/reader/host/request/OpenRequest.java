package com.onyx.android.sdk.reader.host.request;

import android.os.Build;
import android.util.Log;

import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderDrmCertificateFactory;
import com.onyx.android.sdk.reader.api.ReaderDrmManager;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderPluginOptions;
import com.onyx.android.sdk.reader.common.BaseReaderRequest;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.host.options.BaseOptions;
import com.onyx.android.sdk.reader.host.wrapper.Reader;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class OpenRequest extends BaseReaderRequest {

    private String documentPath;
    private BaseOptions srcOptions;
    private ReaderDrmCertificateFactory factory;
    private volatile boolean verifyDevice;

    public OpenRequest(final String path,
                       final BaseOptions documentOptions,
                       final ReaderDrmCertificateFactory f,
                       boolean verify) {
        super();
        documentPath = path;
        srcOptions = documentOptions;
        factory = f;
        verifyDevice = verify;
    }

    public void execute(final Reader reader) throws Exception {
        if (!checkDevice()) {
            throw new ReaderException(ReaderException.ACTIVATION_FAILED, "");
        }

        final ReaderDocumentOptionsImpl documentOptions = srcOptions.documentOptions();
        ReaderPluginOptions pluginOptions = srcOptions.pluginOptions();
        if (pluginOptions == null) {
            pluginOptions = ReaderPluginOptionsImpl.create(getContext());
        }

        if (!reader.getReaderHelper().selectPlugin(getContext(), documentPath, pluginOptions)) {
            return;
        }

        prepareDrmManager(reader);

        try {
            ReaderDocument document = reader.getPlugin().open(documentPath, documentOptions, pluginOptions);
            reader.getReaderHelper().onDocumentOpened(getContext(), documentPath, document, srcOptions, pluginOptions);
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

    private boolean prepareDrmManager(final Reader reader) {
        if (factory == null) {
            return false;
        }

        ReaderDrmManager manager = reader.getPlugin().createDrmManager();
        if (manager != null) {
            manager.activateDeviceDRM(factory.getDeviceId(), factory.getDrmCertificate());
        }
        return true;
    }

}
