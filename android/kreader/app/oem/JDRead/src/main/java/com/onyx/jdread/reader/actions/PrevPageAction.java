package com.onyx.jdread.reader.actions;

import android.content.Context;
import android.graphics.Rect;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.Reader;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.PageViewUpdateEvent;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.request.PreviousScreenRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class PrevPageAction extends BaseReaderAction {

    @Override
    public void execute(ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        final PreviousScreenRequest request = new PreviousScreenRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                EventBus.getDefault().post(new PageViewUpdateEvent());
                ReaderActivityEventHandler.updateReaderViewInfo(request);
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
