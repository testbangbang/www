package com.onyx.sdk.ebookservice;

/**
 * Created by suicheng on 2017/2/11.
 */

public interface Parser<T> {

    String cleanHtmlContent() throws Exception;

    T generateOutPutObject() throws Exception;
}
