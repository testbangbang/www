package com.onyx.jdread.reader.actions;

import android.content.Context;
import android.graphics.Rect;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.PageViewUpdateEvent;
import com.onyx.jdread.reader.event.ReaderActivityEventHandler;
import com.onyx.jdread.reader.request.NextScreenRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class NextPageAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder, RxCallback baseCallback) {
        final NextScreenRequest request = new NextScreenRequest(readerDataHolder.getReader());
        request.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                readerDataHolder.getEventBus().post(new PageViewUpdateEvent());
                ReaderActivityEventHandler.updateReaderViewInfo(readerDataHolder,request);
            }
        });
    }

    public static Rect getRegionOne(Context context) {
        Rect rect = new Rect();
        rect.left = context.getResources().getInteger(R.integer.next_page_touch_one_region_left);
        rect.top = context.getResources().getInteger(R.integer.next_page_touch_one_region_top);
        rect.right = context.getResources().getInteger(R.integer.next_page_touch_one_region_right);
        rect.bottom = context.getResources().getInteger(R.integer.next_page_touch_one_region_bottom);
        return rect;
    }

    public static Rect getRegionTwo(Context context) {
        Rect rect = new Rect();
        rect.left = context.getResources().getInteger(R.integer.next_page_touch_two_region_left);
        rect.top = context.getResources().getInteger(R.integer.next_page_touch_two_region_top);
        rect.right = context.getResources().getInteger(R.integer.next_page_touch_two_region_right);
        rect.bottom = context.getResources().getInteger(R.integer.next_page_touch_two_region_bottom);
        return rect;
    }
}