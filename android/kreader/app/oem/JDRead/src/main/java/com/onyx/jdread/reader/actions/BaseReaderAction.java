package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 17/11/13.
 */

public abstract class BaseReaderAction {
    public abstract void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback);
}
