package com.onyx.reader.common;

import android.util.Log;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class Benchmark {
    private long benchmarkStart = 0;
    private long benchmarkEnd = 0;

    public Benchmark() {
        restart();
    }

    public void restart() {
        benchmarkStart = System.nanoTime();
    }

    public void report(final String tag) {
        Log.i(tag, String.valueOf(duration()));
    }

    public long duration() {
        benchmarkEnd = System.nanoTime();
        return benchmarkEnd - benchmarkStart;
    }
}
