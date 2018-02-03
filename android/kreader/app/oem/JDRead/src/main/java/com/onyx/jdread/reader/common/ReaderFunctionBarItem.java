package com.onyx.jdread.reader.common;

import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.model.FunctionBarItem;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/4.
 */

public class ReaderFunctionBarItem extends FunctionBarItem {
    public Object event;
    public ReaderFunctionBarItem(ViewConfig.FunctionModule functionModule, String firstFragmentName, String itemName, int drawableRes,Object event, boolean backControl) {
        super(functionModule, firstFragmentName, itemName, drawableRes, backControl);
        this.event = event;
    }

    @Override
    public void tabClicked() {
        EventBus.getDefault().post(event);
    }
}
