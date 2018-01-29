package com.onyx.jdread.reader.event;

/**
 * Created by huxiaomao on 2018/1/26.
 */

public class TextTranslateResultEvent {
    private String translateResult;

    public TextTranslateResultEvent(String translateResult) {
        this.translateResult = translateResult;
    }

    public String getTranslateResult() {
        return translateResult;
    }
}
