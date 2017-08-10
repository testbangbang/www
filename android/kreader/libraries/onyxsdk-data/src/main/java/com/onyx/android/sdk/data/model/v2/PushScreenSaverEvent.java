package com.onyx.android.sdk.data.model.v2;

/**
 * Created by suicheng on 2017/8/5.
 */

public class PushScreenSaverEvent extends PushFileEvent {
    public static final int ALL_SET = -1;
    public int index;

    public boolean convertToGrayScale = true;

    public boolean isAllSet() {
        return index <= ALL_SET;
    }
}
