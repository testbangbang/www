package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.reader.common.ReaderViewInfo;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.event.UpdateViewSettingEvent;
import com.onyx.jdread.reader.menu.request.GetViewSettingRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/6.
 */

public class GetViewSettingAction extends BaseReaderAction {
    private ReaderViewInfo readerViewInfo;

    public GetViewSettingAction(ReaderViewInfo readerViewInfo) {
        this.readerViewInfo = readerViewInfo;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        final GetViewSettingRequest request = new GetViewSettingRequest(readerViewInfo, readerDataHolder);
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                notifySaveViewSetting(request);
            }
        });
    }

    public void notifySaveViewSetting(GetViewSettingRequest request) {
        ReaderActivityEventHandler.updateViewSetting(request.getSettings(),request.getStyle(),request.getReaderUserDataInfo());
    }
}
