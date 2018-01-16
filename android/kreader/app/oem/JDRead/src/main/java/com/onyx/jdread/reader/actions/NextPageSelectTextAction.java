package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.data.ReaderTextStyle;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.request.NextPageSelectTextRequest;

/**
 * Created by huxiaomao on 2018/1/15.
 */

public class NextPageSelectTextAction extends BaseReaderAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder) {
        ReaderTextStyle style = readerDataHolder.getStyleCopy();

        final NextPageSelectTextRequest request = new NextPageSelectTextRequest(readerDataHolder.getReader(), style);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {

            }
        });
    }
}
