package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;

import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryListResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.event.SubjectListSortKeyChangeEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackdeng on 2017/12/11.
 */

public class CategoryBookListViewModel extends BaseObservable {

    private EventBus eventBus;
    private TitleBarViewModel titleBarViewModel;
    public final ObservableInt currentPosition = new ObservableInt();
    public final ObservableInt totalPage = new ObservableInt();
    public List<ResultBookBean> bookList = new ArrayList<>();
    public final ObservableBoolean sortButtonIsOpen =new ObservableBoolean();
    public final ObservableBoolean allCatIsOpen =new ObservableBoolean();
    public final ObservableBoolean isFree =new ObservableBoolean();
    public List<CategoryListResultBean.CategoryBeanLevelOne.CategoryBeanLevelTwo> categoryItems;
    public final ObservableBoolean sortKeyHotSelected =new ObservableBoolean(true);
    public final ObservableBoolean sortKeyNewestSelected =new ObservableBoolean();
    public final ObservableBoolean sortKeyPriceSelected =new ObservableBoolean();
    public final ObservableInt contentPage = new ObservableInt(0);
    public final ObservableBoolean showIndicatorView =new ObservableBoolean();

    public CategoryBookListViewModel(EventBus eventBus) {
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

    public int getContentPage() {
        return contentPage.get();
    }

    public void setContentPage(int page) {
        contentPage.set(page);
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

    public List<ResultBookBean> getEnsureBookList() {
        if (bookList == null) {
            bookList = new ArrayList<>();
        }
        return bookList;
    }

    public void addBookList(List<ResultBookBean> list, boolean clear) {
        if (clear) {
            getEnsureBookList().clear();
        }
        if (!CollectionUtils.isNullOrEmpty(list)) {
            getEnsureBookList().addAll(list);
        }
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
        onSortKeyClick(CloudApiContext.SearchBook.SORT_KEY_SALES);
    }

    public void onSortKeyNewestClick(){
        onSortKeyClick(CloudApiContext.SearchBook.SORT_KEY_TIME);
    }

    public void onSortKeyPriceClick(){
        onSortKeyClick(CloudApiContext.SearchBook.SORT_KEY_PRICE);
    }

    private void onSortKeyClick(int sortKey) {
        getEventBus().post(new SubjectListSortKeyChangeEvent(sortKey));
        updateSortKeyInfo(sortKey);
    }

    private void changeSortKeySelected(int sortKey){
        sortKeyHotSelected.set(sortKey == CloudApiContext.SearchBook.SORT_KEY_SALES);
        sortKeyNewestSelected.set(sortKey == CloudApiContext.SearchBook.SORT_KEY_TIME);
        sortKeyPriceSelected.set(sortKey == CloudApiContext.SearchBook.SORT_KEY_PRICE);
    }

    private void changeSortKeyTitleShow(int sortKey) {
        int resId = R.string.subject_list_sort_type_hot;
        switch (sortKey) {
            case CloudApiContext.SearchBook.SORT_KEY_SALES:
                break;
            case CloudApiContext.SearchBook.SORT_KEY_TIME:
                resId = R.string.subject_list_sort_type_newest;
                break;
            case CloudApiContext.SearchBook.SORT_KEY_PRICE:
                resId = R.string.subject_list_sort_type_price;
                break;
        }
        titleBarViewModel.rightText3 = ResManager.getString(resId);
    }

    public void updateSortKeyInfo(int sortKey) {
        changeSortKeySelected(sortKey);
        changeSortKeyTitleShow(sortKey);
    }

    public int getSortKeySelected() {
        return sortKeyHotSelected.get() ? CloudApiContext.SearchBook.SORT_KEY_SALES : (
                sortKeyNewestSelected.get() ? CloudApiContext.SearchBook.SORT_KEY_TIME :
                        CloudApiContext.SearchBook.SORT_KEY_PRICE);
    }
}
