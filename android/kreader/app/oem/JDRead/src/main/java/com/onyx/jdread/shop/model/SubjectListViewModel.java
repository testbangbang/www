package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.event.SubjectListSortKeyChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class SubjectListViewModel extends BaseObservable {

    private EventBus eventBus;
    private TitleBarViewModel titleBarViewModel;
    public final ObservableInt currentPosition = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    public List<ResultBookBean> bookList;
    public final ObservableBoolean sortButtonIsOpen =new ObservableBoolean();
    public final ObservableBoolean allCatIsOpen =new ObservableBoolean();
    public final ObservableBoolean isFree =new ObservableBoolean();
    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categoryItems;

    public SubjectListViewModel(EventBus eventBus) {
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

    public List<ResultBookBean> getBookList() {
        return bookList;
    }

    public void setBookList(List<ResultBookBean> bookList) {
        this.bookList = bookList;
        notifyChange();
    }

    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> getCategoryItems() {
        return categoryItems;
    }

    public void setCategoryItems(List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categoryItems) {
        this.categoryItems = categoryItems;
        notifyChange();
    }

    public void onSortKeyHotClick(){
        getEventBus().post(new SubjectListSortKeyChangeEvent(CloudApiContext.SearchBook.SORT_KEY_PRAISE));
    }

    public void onSortKeySalesClick(){
        getEventBus().post(new SubjectListSortKeyChangeEvent(CloudApiContext.SearchBook.SORT_KEY_SALES));
    }

    public void onSortKeyNewestClick(){
        getEventBus().post(new SubjectListSortKeyChangeEvent(CloudApiContext.SearchBook.SORT_KEY_TIME));
    }
}
