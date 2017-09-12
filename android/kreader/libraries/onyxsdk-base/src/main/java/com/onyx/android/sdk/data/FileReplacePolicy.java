package com.onyx.android.sdk.data;

/**
 * Created by suicheng on 2017/9/12.
 */

public enum FileReplacePolicy {
    Ask, Replace, ReplaceAll, Skip, SkipAll;

    public boolean isSkipPolicy() {
        return this == Skip || this == SkipAll;
    }

    public boolean isReplacePolicy() {
        return this == Replace || this == ReplaceAll;
    }
}
