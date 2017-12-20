package com.onyx.jdread.shop.model;

import android.databinding.BaseObservable;

import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CommentEntity;
import com.onyx.jdread.shop.cloud.entity.jdbean.ResultBookBean;
import com.onyx.jdread.shop.event.OnBookDetailTopBackEvent;
import com.onyx.jdread.shop.event.OnBookDetailTopRightEvent;
import com.onyx.jdread.shop.event.OnCopyrightCancelEvent;
import com.onyx.jdread.shop.event.OnCopyrightEvent;
import com.onyx.jdread.shop.event.OnRecommendNextPageEvent;
import com.onyx.jdread.shop.event.OnViewCommentEvent;

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
    private int pageTag;
    private String title;
    private String rightText;
    private boolean isShowRightText;

    public boolean isShowRightText() {
        return isShowRightText;
    }

    public void setShowRightText(boolean showRightText) {
        isShowRightText = showRightText;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
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

    public int getPageTag() {
        return pageTag;
    }

    public void setPageTag(int pageTag) {
        this.pageTag = pageTag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        getEventBus().post(new OnBookDetailTopBackEvent(pageTag));
    }

    public void onTopRightClick() {
        getEventBus().post(new OnBookDetailTopRightEvent(pageTag));
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
        getEventBus().post(new OnViewCommentEvent());
    }

    public void onCopyrightClick() {
        getEventBus().post(new OnCopyrightEvent());
    }

    public void onCopyrightCancelClick() {
        getEventBus().post(new OnCopyrightCancelEvent());
    }

    public void onRecommendNextPageClick() {
        getEventBus().post(new OnRecommendNextPageEvent());
    }

}