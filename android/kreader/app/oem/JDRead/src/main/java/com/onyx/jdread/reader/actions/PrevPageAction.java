package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
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
    public void execute(final ReaderDataHolder readerDataHolder) {
        final PreviousScreenRequest request = new PreviousScreenRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                EventBus.getDefault().post(new PageViewUpdateEvent());
                ReaderActivityEventHandler.updateReaderViewInfo(request);
            }
        });
    }

    public static Rect getRegionOne(){
        Rect rect = new Rect();
        rect.left = JDReadApplication.getInstance().getResources().getInteger(R.integer.prev_page_touch_region_left);
        rect.top = JDReadApplication.getInstance().getResources().getInteger(R.integer.prev_page_touch_region_top);
        rect.right = JDReadApplication.getInstance().getResources().getInteger(R.integer.prev_page_touch_region_right);
        rect.bottom = JDReadApplication.getInstance().getResources().getInteger(R.integer.prev_page_touch_region_bottom);
        return rect;
    }
}
