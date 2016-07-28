package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.data.ReaderDataHolder;

public abstract class BaseAction {

    public abstract void execute(final ReaderDataHolder readerDataHolder);

    public void execute(final ReaderDataHolder readerDataHolder, BaseCallback baseCallback){
    }

}
