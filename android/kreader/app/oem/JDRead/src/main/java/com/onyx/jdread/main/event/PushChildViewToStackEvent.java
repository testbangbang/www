package com.onyx.jdread.main.event;

import android.os.Bundle;

/**
 * Created by huxiaomao on 2017/12/8.
 */

public class PushChildViewToStackEvent {
    public String childClassName;
    public Bundle bundle;

    public PushChildViewToStackEvent(String childClassName, Bundle bundle) {
        this.childClassName = childClassName;
        this.bundle = bundle;
    }
}
