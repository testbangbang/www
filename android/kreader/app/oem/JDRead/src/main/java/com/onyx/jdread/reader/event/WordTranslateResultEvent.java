package com.onyx.jdread.reader.event;

/**
 * Created by huxiaomao on 2018/1/26.
 */

public class WordTranslateResultEvent {
    private String translateResult;

    public WordTranslateResultEvent(String translateResult) {
        this.translateResult = translateResult;
    }

    public String getTranslateResult() {
        return translateResult;
    }
}
