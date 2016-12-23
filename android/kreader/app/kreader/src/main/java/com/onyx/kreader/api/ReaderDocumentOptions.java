package com.onyx.kreader.api;

/**
 * Created by zhuzeng on 10/2/15.
 * Defined in host and used by plugin.
 */
public interface ReaderDocumentOptions {

    String getDocumentPassword();

    String getCompressedPassword();

    String getLanguage();

    int getAutoCodePage();

    String getCodePageFallback();

}
