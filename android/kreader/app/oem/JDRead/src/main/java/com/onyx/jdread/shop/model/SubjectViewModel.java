package com.onyx.jdread.shop.model;

import com.onyx.jdread.shop.event.ViewAllClickEvent;
import com.onyx.jdread.shop.event.ViewAllNextClickEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class SubjectViewModel extends BaseSubjectViewModel {

    public SubjectViewModel(EventBus eventBus) {
        setEventBus(eventBus);
        setSubjectType(SubjectType.TYPE_COVER);
    }

    public void onViewAllClick() {
        getEventBus().post(new ViewAllClickEvent(getModelBean()));
    }

    public void onNextViewAllClick() {
        getEventBus().post(new ViewAllNextClickEvent(getModelBean()));
    }
}