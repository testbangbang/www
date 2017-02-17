package com.onyx.android.sdk.reader.host.impl;

import com.onyx.android.sdk.reader.api.ReaderChineseConvertType;
import com.onyx.android.sdk.reader.api.ReaderDocumentOptions;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderDocumentOptionsImpl implements ReaderDocumentOptions {

    private String documentPath;
    private String documentPassword;
    private String archivePassword;
    private int codePage;
    private int codePageFallback;
    private String language;
    private ReaderChineseConvertType chineseConvertType = ReaderChineseConvertType.NONE;

    public ReaderDocumentOptionsImpl(final String dp, final String ap) {
        documentPassword = dp;
        archivePassword = ap;
        codePage = -1;
    }

    public ReaderDocumentOptionsImpl(final String dp, final String ap,
                                     final int codePage, final int codePageFallback,
                                     final ReaderChineseConvertType chineseConvertType) {
        documentPassword = dp;
        archivePassword = ap;
        this.codePage = codePage;
        this.codePageFallback = codePageFallback;
        this.chineseConvertType = chineseConvertType;
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
        return codePage;
    }

    public void setCodePage(int codePage) {
        this.codePage = codePage;
    }

    @Override
    public int getCodePageFallback() {
        return codePageFallback;
    }

    public void setCodePageFallback(int codePageFallback) {
        this.codePageFallback = codePageFallback;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public ReaderChineseConvertType getChineseConvertType() {
        return chineseConvertType;
    }

    public void setChineseConvertType(ReaderChineseConvertType chineseConvertType) {
        this.chineseConvertType = chineseConvertType;
    }
}
