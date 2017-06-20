package com.onyx.android.sdk.utils;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class Benchmark {
    private long benchmarkStart = 0;
    private long benchmarkEnd = 0;

    private static Benchmark sInstance = new Benchmark();

    public static Benchmark globalBenchmark() {
        return sInstance;
    }

    public Benchmark() {
        restart();
    }

    public void restart() {
        benchmarkStart = System.currentTimeMillis();
    }

    public void report(final String msg) {
        Debug.i(getClass(), msg + " ---> " + String.valueOf(duration()) + "ms");
    }

    public void reportError(final String msg) {
        Debug.e(getClass(), msg + " ---> " + String.valueOf(duration()) + "ms");
    }

    public long duration() {
        benchmarkEnd = System.currentTimeMillis();
        return benchmarkEnd - benchmarkStart;
    }
}
