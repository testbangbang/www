package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.PageViewUpdateEvent;
import com.onyx.jdread.reader.request.GotoPageRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/8.
 */

public class GotoPageAction extends BaseReaderAction {
    private int page;

    public GotoPageAction(int page) {
        this.page = page;
    }

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        new GotoPageRequest(readerDataHolder,page).execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                EventBus.getDefault().post(new PageViewUpdateEvent());
            }
        });
    }
}
