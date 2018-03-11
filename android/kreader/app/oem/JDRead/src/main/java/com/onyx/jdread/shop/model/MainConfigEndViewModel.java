package com.onyx.jdread.shop.model;

import android.databinding.ObservableBoolean;

import com.onyx.jdread.shop.event.ShopBackTopClick;
import com.onyx.jdread.shop.event.ShopMainViewAllBookEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2018/2/1.
 */

public class MainConfigEndViewModel extends BaseSubjectViewModel{

    public final ObservableBoolean showEmptyView = new ObservableBoolean();

    public MainConfigEndViewModel(EventBus eventBus) {
        setEventBus(eventBus);
        setSubjectType(SubjectType.TYPE_END);
    }

    public void onViewAllBookViewClick() {
        getEventBus().post(new ShopMainViewAllBookEvent());
    }

    public void onBackTopViewClick() {
        getEventBus().post(new ShopBackTopClick());
    }
}
