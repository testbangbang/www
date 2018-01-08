package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CommentEntity;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.event.BookDetailReadNowEvent;
import com.onyx.jdread.shop.event.CopyrightCancelEvent;
import com.onyx.jdread.shop.event.CopyrightEvent;
import com.onyx.jdread.shop.event.DownloadWholeBookEvent;
import com.onyx.jdread.shop.event.RecommendNextPageEvent;
import com.onyx.jdread.shop.event.ViewCommentEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/8.
 */

public class BookDetailViewModel extends BaseObservable {

    private BookDetailResultBean bookDetailResultBean;
    private List<ResultBookBean> recommendList;
    private EventBus eventBus;
    private List<CommentEntity> commentItems;
    private int currentPage;
    private int totalPage;
    private TitleBarViewModel titleBarViewModel;

    public TitleBarViewModel getTitleBarViewModel() {
        return titleBarViewModel;
    }

    public List<CommentEntity> getCommentItems() {
        return commentItems;
    }

    public void setCommentItems(List<CommentEntity> commentItems) {
        this.commentItems = commentItems;
        notifyChange();
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

    public List<ResultBookBean> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<ResultBookBean> recommendList) {
        this.recommendList = recommendList;
        notifyChange();
    }

    public BookDetailViewModel(EventBus eventBus) {
        this.eventBus = eventBus;
        titleBarViewModel = new TitleBarViewModel();
        titleBarViewModel.setEventBus(eventBus);
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

    public void onNowReadClick() {
        getEventBus().post(new BookDetailReadNowEvent(bookDetailResultBean.detail));
    }

    public void onDownBookClick() {
        getEventBus().post(new DownloadWholeBookEvent(bookDetailResultBean.detail));
    }

    public void onShoppingCartClick() {

    }

    public void onViewDirectoryClick() {

    }

    public void onViewCommentClick() {
        getEventBus().post(new ViewCommentEvent());
    }

    public void onCopyrightClick() {
        getEventBus().post(new CopyrightEvent());
    }

    public void onCopyrightCancelClick() {
        getEventBus().post(new CopyrightCancelEvent());
    }

    public void onRecommendNextPageClick() {
        getEventBus().post(new RecommendNextPageEvent());
    }

}