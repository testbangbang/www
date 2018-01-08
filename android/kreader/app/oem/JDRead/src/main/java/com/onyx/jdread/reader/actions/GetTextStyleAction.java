package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.common.GetPageViewInfoCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.menu.request.GetDocumentSettingRequest;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class GetTextStyleAction extends BaseReaderAction {
    private GetPageViewInfoCallback callback;

    public GetTextStyleAction(GetPageViewInfoCallback callback) {
        this.callback = callback;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        final GetDocumentSettingRequest request = new GetDocumentSettingRequest(readerDataHolder);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                if(callback != null){
                    callback.setStyle(request.getStyle());
                    callback.setImageReflowSettings(request.getSettings());
                }
            }
        });
    }
}
