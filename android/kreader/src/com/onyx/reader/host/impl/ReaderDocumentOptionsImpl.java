package com.onyx.reader.host.impl;

import com.onyx.reader.api.ReaderDocumentOptions;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderDocumentOptionsImpl implements ReaderDocumentOptions {

    private String documentPassword;
    private String archivePassword;

    public ReaderDocumentOptionsImpl(final String dp, final String ap) {
        documentPassword = dp;
        archivePassword = ap;
    }

    public String getDocumentPassword() {
        return documentPassword;
    }

    public String getCompressedPassword() {
        return archivePassword;
    }

}
