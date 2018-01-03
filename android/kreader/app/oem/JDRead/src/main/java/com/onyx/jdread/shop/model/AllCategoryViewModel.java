package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableInt;

import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.OnCategoryBoyClick;
import com.onyx.jdread.shop.event.OnCategoryGirlClick;
import com.onyx.jdread.shop.event.OnCategoryPublishClick;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class AllCategoryViewModel extends BaseObservable{

    private EventBus eventBus;
    private TitleBarViewModel titleBarViewModel;
    public final ObservableInt currentPosition = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    public List<CategoryListResultBean.CatListBean> allCategoryItems;
    public List<CategoryListResultBean.CatListBean> topCategoryItems;
    public List<CategoryListResultBean.CatListBean> bottomCategoryItems;

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
        return currentPosition.get();
    }

    public void setCurrentPage(int curPage) {
        currentPosition.set(curPage);
    }

    public int getTotalPage() {
        return totalPage.get();
    }

    public void setTotalPage(int allPage) {
        totalPage.set(allPage);
    }

    public List<CategoryListResultBean.CatListBean> getAllCategoryItems() {
        return allCategoryItems;
    }

    public void setAllCategoryItems(List<CategoryListResultBean.CatListBean> allCategoryItems) {
        this.allCategoryItems = allCategoryItems;
        notifyChange();
    }

    public List<CategoryListResultBean.CatListBean> getTopCategoryItems() {
        return topCategoryItems;
    }

    public void setTopCategoryItems(List<CategoryListResultBean.CatListBean> topCategoryItems) {
        this.topCategoryItems = topCategoryItems;
        notifyChange();
    }

    public List<CategoryListResultBean.CatListBean> getBottomCategoryItems() {
        return bottomCategoryItems;
    }

    public void setBottomCategoryItems(List<CategoryListResultBean.CatListBean> bottomCategoryItems) {
        this.bottomCategoryItems = bottomCategoryItems;
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