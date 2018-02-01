package com.onyx.jdread.reader.menu.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ReaderDataHolder;

/**
 * Created by huxiaomao on 2018/2/1.
 */

public class GetSearchHistoryAction extends BaseReaderAction {
    private int count;

    public GetSearchHistoryAction(int count) {
        this.count = count;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {

    }
}
