package com.onyx.jdread.reader.actions;

import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 17/11/13.
 */

public abstract class BaseReaderAction {
    public ActionCallBack callBack;
    public abstract void execute(ReaderDataHolder readerDataHolder);
    public interface ActionCallBack{
        void onFinally(String pagePosition);
    }
}
