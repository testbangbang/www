package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.SearchBooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestSearchBooks;

import static com.onyx.jdread.shop.common.CloudApiContext.CategoryLevel2BookList.PAGE_SIZE_DEFAULT_VALUES;
import static com.onyx.jdread.shop.common.CloudApiContext.SearchBook.SEARCH_TYPE_BOOK_SHOP;

/**
 * Created by jackdeng on 2018/1/2.
 */

public class BookCategoryLevel2BooksAction extends BaseAction<ShopDataBundle> {

    private int sortType;
    private int sortKey;
    private int currentPage;
    private String catId;
    private BookShopViewModel shopViewModel;
    private BookModelBooksResultBean resultBean;

    public BookCategoryLevel2BooksAction(String catId, int currentPage, int sortKey, int sortType) {
        this.currentPage = currentPage;
        this.catId = catId;
        this.sortType = sortType;
        this.sortKey = sortKey;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        SearchBooksRequestBean requestBean = new SearchBooksRequestBean();
        JDAppBaseInfo jdAppBaseInfo = JDReadApplication.getInstance().getJDAppBaseInfo();
        jdAppBaseInfo.setTime();
        requestBean.setAppBaseInfo(jdAppBaseInfo);
        requestBean.search_type = SEARCH_TYPE_BOOK_SHOP;
        requestBean.cid = String.valueOf(catId);
        requestBean.sort = sortKey + "_" + sortType;
        requestBean.page = String.valueOf(currentPage);
        requestBean.page_size = PAGE_SIZE_DEFAULT_VALUES;
        final RxRequestSearchBooks request = new RxRequestSearchBooks();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestSearchBooks>() {

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
            public void onNext(RxRequestSearchBooks request) {
                resultBean = request.getResultBean();
                if (resultBean != null) {
                    if (resultBean.data != null)
                    shopViewModel.getAllCategoryViewModel().getSubjectListViewModel().setBookList(resultBean.data.items);
                }

                if (rxCallback != null) {
                    rxCallback.onNext(BookCategoryLevel2BooksAction.this);
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

    public BookModelBooksResultBean getBooksResultBean() {
        return resultBean;
    }
}