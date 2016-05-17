package com.onyx.kreader.common;

import android.util.Log;
import com.onyx.kreader.BuildConfig;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class Benchmark {
    private static final String TAG = Benchmark.class.getSimpleName();

    private long benchmarkStart = 0;
    private long benchmarkEnd = 0;

    public Benchmark() {
        restart();
    }

    public void restart() {
        benchmarkStart = System.currentTimeMillis();
    }

    public void report(final String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg + " ---> " + String.valueOf(duration()) + "ms");
        }
    }

    public long duration() {
        benchmarkEnd = System.currentTimeMillis();
        return benchmarkEnd - benchmarkStart;
    }
}
