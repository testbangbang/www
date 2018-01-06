package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.GetTextStyleRequest;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class GetTextStyleAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        final GetTextStyleRequest getTextStyleRequest = new GetTextStyleRequest(readerDataHolder);
        getTextStyleRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getReader().getReaderHelper().setStyle(getTextStyleRequest.getStyle());
            }
        });
    }
}
