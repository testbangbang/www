package com.onyx.kreader.host.impl;

import com.onyx.kreader.api.ReaderDocumentOptions;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderDocumentOptionsImpl implements ReaderDocumentOptions {

    private String documentPath;
    private String documentPassword;
    private String archivePassword;
    private int autoCodePage;
    private String codePageFallback;
    private String language;


    public ReaderDocumentOptionsImpl(final String dp, final String ap,
                                     final int codePage) {
        documentPassword = dp;
        archivePassword = ap;
        autoCodePage = codePage;
    }

    public void setDocumentPath(final String path) {
        documentPath = path;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public String getDocumentPassword() {
        return documentPassword;
    }

    public void setDocumentPassword(final String password) {
        documentPassword = password;
    }

    public String getCompressedPassword() {
        return archivePassword;
    }

    public void setCompressedPassword(final String password) {
        archivePassword = password;
    }

    @Override
    public int getCodePage() {
        return autoCodePage;
    }

    public void setAutoCodePage(int autoCodePage) {
        this.autoCodePage = autoCodePage;
    }

    @Override
    public String getCodePageFallback() {
        return codePageFallback;
    }

    public void setCodePageFallback(String codePageFallback) {
        this.codePageFallback = codePageFallback;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
