package com.onyx.jdread.main.event;

import com.onyx.jdread.main.model.FunctionBarItem;

/**
 * Created by suicheng on 2018/1/31.
 */

public class TabLongClickedEvent {

    public FunctionBarItem functionItem;

    public TabLongClickedEvent(FunctionBarItem item) {
        this.functionItem = item;
    }
}
