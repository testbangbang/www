package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;

import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.event.CategoryTitleTwoClick;
import com.onyx.jdread.shop.event.CategoryTitleThreeClick;
import com.onyx.jdread.shop.event.CategoryTitleOneClick;

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
    public final ObservableField<String> titleOne = new ObservableField<>();
    public final ObservableField<String> titleTwo = new ObservableField<>();
    public final ObservableField<String> titleThree = new ObservableField<>();
    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> allCategoryItems;
    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> topCategoryItems;
    public CategoryBookListViewModel categoryBookListViewModel;

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
        curPage = Math.max(1,curPage);
        currentPosition.set(curPage);
    }

    public int getTotalPage() {
        return totalPage.get();
    }

    public void setTotalPage(int allPage) {
        allPage = Math.max(1,allPage);
        totalPage.set(allPage);
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getAllCategoryItems() {
        return allCategoryItems;
    }

    public void setAllCategoryItems(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> allCategoryItems) {
        this.allCategoryItems = allCategoryItems;
        notifyChange();
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getTopCategoryItems() {
        return topCategoryItems;
    }

    public void setTopCategoryItems(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> topCategoryItems) {
        this.topCategoryItems = topCategoryItems;
        notifyChange();
    }

    public CategoryBookListViewModel getCategoryBookListViewModel() {
        if (categoryBookListViewModel == null){
            categoryBookListViewModel = new CategoryBookListViewModel(getEventBus());
        }
        return categoryBookListViewModel;
    }

    public void onTitleOneClick(){
        postEvent(new CategoryTitleOneClick());
    }

    public void onTitleTwoClick(){
        postEvent(new CategoryTitleTwoClick());
    }

    public void onTitleThreeClick(){
        postEvent(new CategoryTitleThreeClick());
    }

    private void postEvent(Object event) {
        getEventBus().post(event);
    }
}