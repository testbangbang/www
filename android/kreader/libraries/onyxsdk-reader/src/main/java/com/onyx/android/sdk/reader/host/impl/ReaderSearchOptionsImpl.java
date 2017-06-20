package com.onyx.android.sdk.reader.host.impl;

import com.onyx.android.sdk.reader.api.ReaderSearchOptions;

/**
 * Created by zhuzeng on 2/15/16.
 */
public class ReaderSearchOptionsImpl implements ReaderSearchOptions {

    private String pattern;
    private boolean caseSensitive;
    private boolean matchWholeWord;
    private String pageName;
    private int contextLength = 100;

    public ReaderSearchOptionsImpl(final String page, final String text, boolean cs, boolean wholeWord) {
        pageName = page;
        pattern = text;
        caseSensitive = cs;
        matchWholeWord = wholeWord;
    }

    public String pattern() {
        return pattern;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public boolean isMatchWholeWord() {
        return matchWholeWord;
    }

    public String fromPage() {
        return pageName;
    }

    @Override
    public int contextLength() {
        return contextLength;
    }

    public void setContextLength(int contextLength) {
        this.contextLength = contextLength;
    }
}
