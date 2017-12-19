package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.common.Constants;
import com.onyx.jdread.shop.cloud.entity.BookCommentsRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookCommentsResultBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CommentEntity;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookDetailViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestGetBookCommentList;

import java.util.List;

/**
 * Created by jackdeng on 2017/12/13.
 */

public class BookCommentListAction extends BaseAction<ShopDataBundle> {

    private int currentPage;
    private long bookID;
    private BookCommentsResultBean bookCommentsResultBean;

    public BookCommentListAction(long bookID, int currentPage) {
        this.bookID = bookID;
        this.currentPage = currentPage;
    }

    public BookCommentsResultBean getbookCommentsBean() {
        return bookCommentsResultBean;
    }

    @Override
    public void execute(ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        final BookDetailViewModel bookDetailViewModel = shopDataBundle.getBookDetailViewModel();
        BookCommentsRequestBean bookCommentsRequestBean = new BookCommentsRequestBean();
        bookCommentsRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        String bookCommentsJsonBody = getBookCommentsJsonBody(CloudApiContext.RecommendList.BOOK_TYPE, bookID, currentPage);
        bookCommentsRequestBean.setBody(bookCommentsJsonBody);
        final RxRequestGetBookCommentList rq = new RxRequestGetBookCommentList();
        rq.setBookCommentsRequestBean(bookCommentsRequestBean);
        rq.execute(new RxCallback<RxRequestGetBookCommentList>() {
            @Override
            public void onNext(RxRequestGetBookCommentList request) {
                bookCommentsResultBean = request.getBookCommentsResultBean();
                if (bookCommentsResultBean != null && bookCommentsResultBean.getReviews() != null) {
                    List<CommentEntity> commentItems = bookCommentsResultBean.getReviews().getList();
                    bookDetailViewModel.setCommentItems(commentItems);
                }
                if (rxCallback != null) {
                    rxCallback.onNext(BookCommentListAction.this);
                    rxCallback.onComplete();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                if (rxCallback != null) {
                    rxCallback.onError(throwable);
                }
            }
        });
    }

    private String getBookCommentsJsonBody(String bookType, long eBookId, int currentPage) {
        final JSONObject json = new JSONObject();
        try {
            if (bookType.equals(CloudApiContext.RecommendList.BOOK_TYPE))
                json.put(CloudApiContext.RecommendList.BOOK_TYPE_ID, eBookId);
            else {
                json.put(CloudApiContext.RecommendList.PAGE_BOOK_ID, eBookId);
            }
            json.put(CloudApiContext.SearchBook.CURRENT_PAGE, currentPage);
            json.put(CloudApiContext.SearchBook.PAGE_SIZE, Constants.BOOK_COMMENT_PAGE_SIZE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }
}
