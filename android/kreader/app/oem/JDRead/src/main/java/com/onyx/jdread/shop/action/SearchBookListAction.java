package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.SearchBooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.BookShopViewModel;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestSearchBooks;

import java.util.HashMap;
import java.util.Map;

import static com.onyx.jdread.shop.common.CloudApiContext.CategoryLevel2BookList.PAGE_SIZE_DEFAULT_VALUES;
import static com.onyx.jdread.shop.common.CloudApiContext.SearchBook.SEARCH_TYPE_BOOK_SHOP;

/**
 * Created by jackdeng on 2018/1/2.
 */

public class SearchBookListAction extends BaseAction<ShopDataBundle> {

    private int sortType;
    private int sortKey;
    private int currentPage;
    private String catId;
    private String keyWord;
    private int filter;
    private BookShopViewModel shopViewModel;
    private BookModelBooksResultBean resultBean;

    public SearchBookListAction(String catId, int currentPage, int sortKey, int sortType, String keyWord, int filter) {
        this.currentPage = currentPage;
        this.catId = catId;
        this.sortType = sortType;
        this.sortKey = sortKey;
        this.keyWord = keyWord;
        this.filter = filter;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        shopViewModel = shopDataBundle.getShopViewModel();
        SearchBooksRequestBean requestBean = new SearchBooksRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.SearchBook.SEARCH_TYPE, SEARCH_TYPE_BOOK_SHOP);
        queryArgs.put(CloudApiContext.SearchBook.CATE_ID, String.valueOf(catId));
        queryArgs.put(CloudApiContext.SearchBook.KEY_WORD, keyWord);
        queryArgs.put(CloudApiContext.SearchBook.FILTER, String.valueOf(filter));
        queryArgs.put(CloudApiContext.SearchBook.SORT, sortKey + "_" + sortType);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, String.valueOf(currentPage));
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, PAGE_SIZE_DEFAULT_VALUES);
        appBaseInfo.addRequestParams(queryArgs);
        appBaseInfo.setSign(appBaseInfo.getSignValue(CloudApiContext.BookShopURI.SEARCH_URI));
        requestBean.setAppBaseInfo(appBaseInfo);
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
                if (rxCallback != null) {
                    rxCallback.onNext(SearchBookListAction.this);
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
