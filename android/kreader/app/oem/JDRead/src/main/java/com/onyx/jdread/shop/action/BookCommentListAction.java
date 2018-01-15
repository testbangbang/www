package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CommentEntity;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetBookCommentList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookCommentListAction extends BaseAction<ShopDataBundle> {

    private int currentPage;
    private long bookID;
    private BookCommentsResultBean.DataBean commentsData;

    public BookCommentListAction(long bookID, int currentPage) {
        this.bookID = bookID;
        this.currentPage = currentPage;
    }

    public BookCommentsResultBean.DataBean getbookCommentsBean() {
        return commentsData;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = shopDataBundle.getBookDetailViewModel();
        BookCommentsRequestBean bookCommentsRequestBean = new BookCommentsRequestBean();
        JDAppBaseInfo jdAppBaseInfo = JDReadApplication.getInstance().getJDAppBaseInfo();
        jdAppBaseInfo.setTime();
        Map<String, String> queryArgs = new HashMap();
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, Constants.BOOK_PAGE_SIZE);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, String.valueOf(currentPage));
        bookCommentsRequestBean.setAppBaseInfo(jdAppBaseInfo);
        bookCommentsRequestBean.bookId = bookID;
        bookCommentsRequestBean.setQueryArgsMap(queryArgs);
        final RxRequestGetBookCommentList rq = new RxRequestGetBookCommentList();
        rq.setBookCommentsRequestBean(bookCommentsRequestBean);
        rq.execute(new RxCallback<RxRequestGetBookCommentList>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(shopDataBundle, R.string.loading);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(shopDataBundle);
            }

            @Override
            public void onNext(RxRequestGetBookCommentList request) {
                BookCommentsResultBean bookCommentsResultBean = request.getBookCommentsResultBean();
                if (bookCommentsResultBean != null && bookCommentsResultBean.data != null) {
                    commentsData = bookCommentsResultBean.data;
                    List<CommentEntity> commentItems = commentsData.comments;
                    bookDetailViewModel.setCommentItems(commentItems);
                }
                if (rxCallback != null) {
                    rxCallback.onNext(BookCommentListAction.this);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }

            @Override
            public void onComplete() {
                super.onComplete();
                if (rxCallback != null) {
                    rxCallback.onComplete();
                }
            }
        });
    }
}
