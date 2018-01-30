package com.onyx.jdread.reader.actions;

import com.onyx.android.sdk.data.PageInfo;
import com.onyx.android.sdk.data.model.Bookmark;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.reader.common.ReaderUserDataInfo;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.InitPageViewInfoEvent;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.menu.event.ToggleBookmarkSuccessEvent;
import com.onyx.jdread.reader.request.AddBookmarkRequest;
import com.onyx.jdread.reader.request.DeleteBookmarkRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2018/1/9.
 */

public class ToggleBookmarkAction extends  BaseReaderAction{
    public enum ToggleSwitch {On, Off}

    private ToggleSwitch toggleSwitch;
    private ReaderUserDataInfo readerUserDataInfo;
    private PageInfo pageInfo;

    public ToggleBookmarkAction(ToggleSwitch toggleSwitch, ReaderUserDataInfo readerUserDataInfo, PageInfo pageInfo) {
        this.toggleSwitch = toggleSwitch;
        this.readerUserDataInfo = readerUserDataInfo;
        this.pageInfo = pageInfo;
    }

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        if (toggleSwitch == ToggleSwitch.On) {
            final AddBookmarkRequest request = new AddBookmarkRequest(readerDataHolder.getReader(),pageInfo);
            request.execute(new RxCallback() {
                @Override
                public void onNext(Object o) {
                    ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);
                    notifyResult(readerDataHolder.getEventBus());
                }

                @Override
                public void onError(Throwable throwable) {
                    ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
                }
            });
        } else if (toggleSwitch == ToggleSwitch.Off) {
            Bookmark bookmark = readerUserDataInfo.getBookmark(pageInfo);
            final DeleteBookmarkRequest request = new DeleteBookmarkRequest(readerDataHolder.getReader(),bookmark);
            request.execute(new RxCallback() {
                @Override
                public void onNext(Object o) {
                    ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);
                    notifyResult(readerDataHolder.getEventBus());
                }

                @Override
                public void onError(Throwable throwable) {
                    ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
                }
            });
        }
    }

    private void notifyResult(EventBus eventBus){
        ToggleBookmarkSuccessEvent event = new ToggleBookmarkSuccessEvent(toggleSwitch);
        eventBus.post(event);
    }
}
