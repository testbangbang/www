package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.CategoryLevel2BooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryLevel2BooksResultBean;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryLevel2Books;

import static com.onyx.jdread.shop.common.CloudApiContext.CategoryLevel2BookList.PAGE_SIZE_DEFAULT_VALUES;
import static com.onyx.jdread.shop.common.CloudApiContext.SearchBook.SEARCH_TYPE_BOOK_SHOP;

/**
 * Created by jackdeng on 2018/1/2.
 */

public class BookCategoryLevel2BooksAction extends BaseAction<ShopDataBundle> {

    private int sortType;
    private int sortKey;
    private int currentPage;
    private int catId;
    private BookShopViewModel shopViewModel;
    private CategoryLevel2BooksResultBean categoryLevel2BooksResultBean;

    public BookCategoryLevel2BooksAction(int catId, int currentPage, int sortKey, int sortType) {
        this.currentPage = currentPage;
        this.catId = catId;
        this.sortType = sortType;
        this.sortKey = sortKey;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        CategoryLevel2BooksRequestBean requestBean = new CategoryLevel2BooksRequestBean();
        JDAppBaseInfo jdAppBaseInfo = JDReadApplication.getInstance().getJDAppBaseInfo();
        jdAppBaseInfo.setTime(String.valueOf(System.currentTimeMillis()));
        requestBean.setAppBaseInfo(jdAppBaseInfo);
        requestBean.search_type = SEARCH_TYPE_BOOK_SHOP;
        requestBean.cid = String.valueOf(catId);
        requestBean.sort = sortKey + "_" + sortType;
        requestBean.page = String.valueOf(currentPage);
        requestBean.page_size = PAGE_SIZE_DEFAULT_VALUES;
        final RxRequestCategoryLevel2Books request = new RxRequestCategoryLevel2Books();
        request.setRequestBean(requestBean);
        request.execute(new RxCallback<RxRequestCategoryLevel2Books>() {

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
            public void onNext(RxRequestCategoryLevel2Books request) {
                categoryLevel2BooksResultBean = request.getCategoryLevel2BooksResultBean();
                if (categoryLevel2BooksResultBean != null) {
                    shopViewModel.getAllCategoryViewModel().getSubjectListViewModel().setBookList(categoryLevel2BooksResultBean.bookList);
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

    public CategoryLevel2BooksResultBean getBooksResultBean() {
        return categoryLevel2BooksResultBean;
    }
}