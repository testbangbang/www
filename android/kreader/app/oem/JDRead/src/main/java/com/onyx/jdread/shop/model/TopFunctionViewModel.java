package com.onyx.jdread.shop.model;

import com.onyx.jdread.shop.event.CategoryViewClick;
import com.onyx.jdread.shop.event.EnjoyReadViewClick;
import com.onyx.jdread.shop.event.NewBookViewClick;
import com.onyx.jdread.shop.event.RankViewClick;
import com.onyx.jdread.shop.event.SaleViewClick;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2018/1/31.
 */

public class TopFunctionViewModel extends BaseSubjectViewModel {

    public TopFunctionViewModel(EventBus eventBus) {
        setEventBus(eventBus);
        setSubjectType(SubjectType.TYPE_TOP_FUNCTION);
        setPageIndex(0);
    }

    public void onRankViewClick() {
        getEventBus().post(new RankViewClick());
    }

    public void onEnjoyReadViewClick() {
        getEventBus().post(new EnjoyReadViewClick());
    }

    public void onSaleViewClick() {
        getEventBus().post(new SaleViewClick());
    }

    public void onNewBookViewClick() {
        getEventBus().post(new NewBookViewClick());
    }

    public void onCategoryViewClick() {
        getEventBus().post(new CategoryViewClick());
    }
}
