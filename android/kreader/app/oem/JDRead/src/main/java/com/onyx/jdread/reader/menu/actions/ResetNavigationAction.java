package com.onyx.jdread.reader.menu.actions;


import com.onyx.android.sdk.data.PageConstants;
import com.onyx.android.sdk.reader.host.navigation.NavigationArgs;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.actions.BaseReaderAction;
import com.onyx.jdread.reader.data.ChangeLayoutParameter;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.request.ChangeLayoutRequest;

/**
 * Created by huxiaomao on 2018/1/1.
 */

public class ResetNavigationAction extends BaseReaderAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        ChangeLayoutParameter parameter = new ChangeLayoutParameter(PageConstants.SINGLE_PAGE, new NavigationArgs());
        final ChangeLayoutRequest request = new ChangeLayoutRequest(readerDataHolder.getReader(), parameter, null);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(request);
            }
        });
    }
}
