package com.onyx.android.dr.reader.event;

/**
 * Created by huxiaomao on 17/5/16.
 */

public class ChangeSpeechRateEvent {
    private float rate;

    public ChangeSpeechRateEvent(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
