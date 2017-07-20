package com.onyx.android.dr.reader.event;

import com.onyx.android.sdk.reader.api.ReaderSentence;

/**
 * Created by huxiaomao on 17/5/16.
 */

public class SentenceRequestResultEvent {
    private ReaderSentence currentSentence;
    private String startPosition;
    private Throwable throwable;

    public SentenceRequestResultEvent(ReaderSentence currentSentence,Throwable throwable) {
        this.currentSentence = currentSentence;
        this.throwable = throwable;
    }

    public ReaderSentence getCurrentSentence() {
        return currentSentence;
    }

    public void setCurrentSentence(ReaderSentence currentSentence) {
        this.currentSentence = currentSentence;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(String startPosition) {
        this.startPosition = startPosition;
    }
}
