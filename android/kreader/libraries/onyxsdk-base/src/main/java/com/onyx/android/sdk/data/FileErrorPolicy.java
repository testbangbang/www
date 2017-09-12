package com.onyx.android.sdk.data;

/**
 * Created by suicheng on 2017/9/12.
 */

public enum  FileErrorPolicy {
    Retry, Skip, SkipAll;

    public boolean isSkipPolicy() {
        return this == Skip || this == SkipAll;
    }
}
