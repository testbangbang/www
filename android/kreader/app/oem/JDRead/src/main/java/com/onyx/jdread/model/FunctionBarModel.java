package com.onyx.jdread.model;

import android.databinding.ObservableBoolean;
import android.view.View;

import com.onyx.jdread.common.ViewConfig;
import com.onyx.jdread.event.ChangeChildViewEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/9.
 */

public class FunctionBarModel {
    private static final String TAG = FunctionBarModel.class.getSimpleName();
    private ObservableBoolean isShow = new ObservableBoolean(true);
    private StackList libraryStack;
    private StackList shoppingStack;

    public FunctionBarModel() {
        initLibraryStack();
        initShoppingStack();
    }

    private void initLibraryStack(){
        libraryStack = new StackList();
        ViewConfig.initLibraryStack(libraryStack);
    }

    private void initShoppingStack(){
        shoppingStack = new StackList();
        ViewConfig.initShoppingStack(shoppingStack);
    }

    public ObservableBoolean getIsShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {

    }

    public void libraryClickListener(View view) {
        ChangeChildViewEvent event = new ChangeChildViewEvent();
        event.childViewName = libraryStack.peek();
        EventBus.getDefault().post(event);
    }

    public void shoppingClickListener(View view) {
        ChangeChildViewEvent event = new ChangeChildViewEvent();
        event.childViewName = shoppingStack.peek();
        EventBus.getDefault().post(event);
    }

    public void backClickListener(View view) {

    }

    public void settingClickListener(View view) {

    }

    public void personalClickListener(View view) {

    }
}
