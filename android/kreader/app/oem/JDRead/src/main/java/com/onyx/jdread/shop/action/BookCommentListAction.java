package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookDetailResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CommentEntity;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetBookCommentList;

import java.util.ArrayList;
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
        final BookCommentsRequestBean bookCommentsRequestBean = new BookCommentsRequestBean();
        bookCommentsRequestBean.bookId = bookID;
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, Constants.BOOK_PAGE_SIZE);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, String.valueOf(currentPage));
        appBaseInfo.addRequestParams(queryArgs);
        String sign = String.format(CloudApiContext.BookShopURI.BOOK_COMMENT_LIST_URI, String.valueOf(bookID));
        appBaseInfo.setSign(appBaseInfo.getSignValue(sign));
        bookCommentsRequestBean.setAppBaseInfo(appBaseInfo);
        final RxRequestGetBookCommentList rq = new RxRequestGetBookCommentList();
        rq.setBookCommentsRequestBean(bookCommentsRequestBean);
        rq.execute(new RxCallback<RxRequestGetBookCommentList>() {

            @Override
            public void onSubscribe() {
                bookDetailViewModel.setBookDetailResultBean(new BookDetailResultBean());
                bookDetailViewModel.setCommentItems(new ArrayList<CommentEntity>());
                showLoadingDialog(shopDataBundle, R.string.loading);
                invokeSubscribe(rxCallback);
            }

            @Override
            public void onFinally() {
                hideLoadingDialog(shopDataBundle);
                invokeFinally(rxCallback);
            }

            @Override
            public void onNext(RxRequestGetBookCommentList request) {
                BookCommentsResultBean bookCommentsResultBean = request.getBookCommentsResultBean();
                List<CommentEntity> list = new ArrayList<>();
                if (bookCommentsResultBean != null && bookCommentsResultBean.data != null) {
                    commentsData = bookCommentsResultBean.data;
                    list = commentsData.comments;
                }
                bookDetailViewModel.setCommentItems(list);
                invokeNext(rxCallback, BookCommentListAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                bookDetailViewModel.setBookDetailResultBean(new BookDetailResultBean());
                bookDetailViewModel.setCommentItems(new ArrayList<CommentEntity>());
                PersonalErrorEvent.onErrorHandle(throwable, getClass().getSimpleName(), shopDataBundle.getEventBus());
                invokeError(rxCallback, throwable);
            }

            @Override
            public void onComplete() {
                invokeComplete(rxCallback);
            }
        });
    }
}
