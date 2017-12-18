package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.event.OnRecommendNextPageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookDetailViewModel extends BaseObservable {

    private BookDetailResultBean bookDetailResultBean;
    private List<ResultBookBean> recommendList;
    private EventBus eventBus;

    public List<ResultBookBean> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<ResultBookBean> recommendList) {
        this.recommendList = recommendList;
        notifyChange();
    }

    public BookDetailViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public BookDetailResultBean getBookDetailResultBean() {
        return bookDetailResultBean;
    }

    public void setBookDetailResultBean(BookDetailResultBean bookDetailResultBean) {
        this.bookDetailResultBean = bookDetailResultBean;
        notifyChange();
    }

    public void onTopBackClick() {

    }

    public void onNowReadClick() {

    }

    public void onDownBookClick() {

    }

    public void onShoppingCartClick() {

    }

    public void onViewDirectoryClick() {

    }

    public void onViewCommentClick() {

    }

    public void onCopyrightClick() {

    }

    public void onRecommendNextPageClick() {
        getEventBus().post(new OnRecommendNextPageEvent());
    }

}