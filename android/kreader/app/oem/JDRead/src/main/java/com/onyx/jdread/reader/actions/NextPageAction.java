package com.onyx.jdread.reader.actions;

import android.graphics.Rect;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.reader.data.ReaderDataHolder;
import com.onyx.jdread.reader.event.PageViewUpdateEvent;
import com.onyx.jdread.reader.request.NextScreenRequest;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by huxiaomao on 2017/12/25.
 */

public class NextPageAction extends BaseReaderAction {
    @Override
    public void execute(final ReaderDataHolder readerDataHolder) {
        NextScreenRequest nextScreenRequest = new NextScreenRequest(readerDataHolder);
        nextScreenRequest.execute(new RxCallback() {
            @Override
            public void onNext(Object o) {
                EventBus.getDefault().post(new PageViewUpdateEvent());
            }
        });
    }

    public static Rect getRegionOne(){
        Rect rect = new Rect();
        rect.left = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_one_region_left);
        rect.top = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_one_region_top);
        rect.right = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_one_region_right);
        rect.bottom = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_one_region_bottom);
        return rect;
    }

    public static Rect getRegionTwo(){
        Rect rect = new Rect();
        rect.left = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_two_region_left);
        rect.top = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_two_region_top);
        rect.right = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_two_region_right);
        rect.bottom = JDReadApplication.getInstance().getResources().getInteger(R.integer.next_page_touch_two_region_bottom);
        return rect;
    }
}
