package com.onyx.reader.plugins.adobe;

import com.onyx.reader.api.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class AdobeReaderPlugin implements ReaderPlugin {

    private AdobePluginImpl impl;

    public String displayName() {
        return AdobeReaderPlugin.class.getSimpleName();
    }

    public List<String> supportedFileList() {
        String [] array = {".epub", ".pdf"};
        return Arrays.asList(array);
    }

    public ReaderDocument open(final String path, final ReaderDocumentOptions documentOptions, final ReaderPluginOptions pluginOptions) throws ReaderException {
        return null;
    }

    public boolean supportDrm() {
        return true;
    }

    public ReaderDrmManager createDrmManager() {
        return null;
    }

    public void abortCurrentJob() {

    }

    public void clearAbortFlag() {

    }

    private AdobePluginImpl getPluginImpl() {
        if (impl == null) {
            impl = new AdobePluginImpl();
        }
        return impl;
    }

}
