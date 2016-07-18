package com.onyx.kreader.ui.actions;


import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.kreader.ui.ReaderActivity;

public abstract class BaseAction {

    public abstract void execute(final ReaderActivity readerActivity);

    public void execute(final ReaderActivity readerActivity,BaseCallback baseCallback){
    }

}
