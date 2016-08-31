package com.onyx.android.sdk.data.model;

/**
 * Created by zhuzeng on 6/1/16.
 */
public class ReadingProgress {

    private int current = 0;
    private int total = 1;

    public ReadingProgress(int c, int t) {
        current = c;
        total = t;
    }

}
