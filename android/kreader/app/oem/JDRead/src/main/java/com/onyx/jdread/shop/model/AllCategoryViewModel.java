package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.event.OnCategoryBoyClick;
import com.onyx.jdread.shop.event.OnCategoryGirlClick;
import com.onyx.jdread.shop.event.OnCategoryPublishClick;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class AllCategoryViewModel extends BaseObservable{

    private EventBus eventBus;
    private TitleBarViewModel titleBarViewModel;
    private int currentPage;
    private int totalPage;

    public AllCategoryViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
    }

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        notifyChange();
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
        notifyChange();
    }

    public void onPublishClick(){
        getEventBus().post(new OnCategoryPublishClick());
    }

    public void onBoyClick(){
        getEventBus().post(new OnCategoryBoyClick());
    }

    public void onGirlClick(){
        getEventBus().post(new OnCategoryGirlClick());
    }
}