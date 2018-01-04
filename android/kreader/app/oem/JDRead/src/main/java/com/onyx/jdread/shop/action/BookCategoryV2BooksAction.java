package com.onyx.jdread.shop.action;

import com.alibaba.fastjson.JSONObject;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.shop.cloud.entity.BaseRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.CategoryV2BooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestCategoryV2Books;

/**
 * Created by jackdeng on 2018/1/2.
 */

public class BookCategoryV2BooksAction extends BaseAction<ShopDataBundle> {

    private int sortType;
    private int currentPage;
    private int catId;
    private BookShopViewModel shopViewModel;
    private CategoryV2BooksResultBean categoryV2BooksResultBean;

    public BookCategoryV2BooksAction(int catId, int currentPage, int sortType) {
        this.currentPage = currentPage;
        this.catId = catId;
        this.sortType = sortType;
    }

    @Override
    public void execute(ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        BaseRequestBean baseRequestBean = new BaseRequestBean();
        baseRequestBean.setAppBaseInfo(JDReadApplication.getInstance().getAppBaseInfo());
        JSONObject body = new JSONObject();
        body.put(CloudApiContext.CategoryBookListV2.SORT_TYPE, sortType);
        body.put(CloudApiContext.CategoryBookListV2.PAGE_SIZE, CloudApiContext.CategoryBookListV2.PAGE_SIZE_DEFAULT_VALUES);
        body.put(CloudApiContext.CategoryBookListV2.CAT_ID, catId);
        body.put(CloudApiContext.CategoryBookListV2.CURRENT_PAGE, currentPage);
        body.put(CloudApiContext.CategoryBookListV2.SORT_KEY, CloudApiContext.CategoryBookListV2.SORT_KEY_DEFAULT_VALUES);
        body.put(CloudApiContext.CategoryBookListV2.CLIENT_PLATFORM, CloudApiContext.CategoryBookListV2.CLIENT_PLATFORM_DEFAULT_VALUES);
        body.put(CloudApiContext.CategoryBookListV2.ROOT_ID, CloudApiContext.CategoryBookListV2.ROOT_ID_DEFAULT_VALUES);
        baseRequestBean.setBody(body.toJSONString());
        baseRequestBean.setBody(body.toJSONString());
        final RxRequestCategoryV2Books request = new RxRequestCategoryV2Books();
        request.setBaseRequestBean(baseRequestBean);
        request.execute(new RxCallback<RxRequestCategoryV2Books>() {
            @Override
            public void onNext(RxRequestCategoryV2Books request) {
                categoryV2BooksResultBean = request.getCategoryV2BooksResultBean();
                if (categoryV2BooksResultBean != null) {
                    shopViewModel.getAllCategoryViewModel().getSubjectListViewModel().setBookList(categoryV2BooksResultBean.bookList);
                }

                if (rxCallback != null) {
                    rxCallback.onNext(BookCategoryV2BooksAction.this);
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

    public CategoryV2BooksResultBean getBooksResultBean() {
        return categoryV2BooksResultBean;
    }
}