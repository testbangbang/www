package com.onyx.jdread.reader.actions;

import android.content.Context;
import android.graphics.Rect;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.menu.event.ReaderErrorEvent;
import com.onyx.jdread.reader.request.PreloadNextScreenRequest;
import com.onyx.jdread.reader.request.PreloadPreviousScreenRequest;
import com.onyx.jdread.reader.request.PreviousScreenRequest;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class PrevPageAction extends BaseReaderAction {

    @Override
    public void execute(final ReaderDataHolder readerDataHolder, final RxCallback baseCallback) {
        if(!readerDataHolder.getReaderViewInfo().canPrevScreen){
            ToastUtil.showToast(readerDataHolder.getAppContext(), R.string.first_page);
            return;
        }
        final PreviousScreenRequest request = new PreviousScreenRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);
                PreloadPreviousScreenRequest preloadPreviousScreenRequest = new PreloadPreviousScreenRequest(readerDataHolder.getReader());
                preloadPreviousScreenRequest.execute(null);
                if(baseCallback != null){
                    baseCallback.onNext(o);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                ReaderErrorEvent.onErrorHandle(throwable,this.getClass().getSimpleName(),readerDataHolder.getEventBus());
            }
        });
    }

    public static Rect getRegionOne(Context context){
        Rect rect = new Rect();
        rect.left = context.getResources().getInteger(R.integer.prev_page_touch_region_left);
        rect.top = context.getResources().getInteger(R.integer.prev_page_touch_region_top);
        rect.right = context.getResources().getInteger(R.integer.prev_page_touch_region_right);
        rect.bottom = context.getResources().getInteger(R.integer.prev_page_touch_region_bottom);
        return rect;
    }
}
