package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryLevel2BooksResultBean;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryLevel2Books;

/**
 * Created by jackdeng on 2018/1/2.
 */

public class BookCategoryLevel2BooksAction extends BaseAction<ShopDataBundle> {

    private int sortType;
    private int currentPage;
    private int catId;
    private BookShopViewModel shopViewModel;
    private CategoryLevel2BooksResultBean categoryLevel2BooksResultBean;

    public BookCategoryLevel2BooksAction(int catId, int currentPage, int sortType) {
        this.currentPage = currentPage;
        this.catId = catId;
        this.sortType = sortType;
    }

    @Override
    public void execute(ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        final RxRequestCategoryLevel2Books request = new RxRequestCategoryLevel2Books();
        request.setBaseRequestBean(baseRequestBean, currentPage, catId, sortType);
        request.execute(new RxCallback<RxRequestCategoryLevel2Books>() {
            @Override
            public void onNext(RxRequestCategoryLevel2Books request) {
                categoryLevel2BooksResultBean = request.getCategoryLevel2BooksResultBean();
                if (categoryLevel2BooksResultBean != null) {
                    shopViewModel.getAllCategoryViewModel().getSubjectListViewModel().setBookList(categoryLevel2BooksResultBean.bookList);
                }

                if (rxCallback != null) {
                    rxCallback.onNext(BookCategoryLevel2BooksAction.this);
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

    public CategoryLevel2BooksResultBean getBooksResultBean() {
        return categoryLevel2BooksResultBean;
    }
}