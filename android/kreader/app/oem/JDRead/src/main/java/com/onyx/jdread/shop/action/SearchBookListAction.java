package com.onyx.jdread.shop.action;

import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.jdread.R;
import com.onyx.jdread.shop.cloud.entity.SearchBooksRequestBean;
import com.onyx.jdread.shop.cloud.entity.jdbean.BookModelBooksResultBean;
import com.onyx.jdread.shop.common.CloudApiContext;
import com.onyx.jdread.shop.common.JDAppBaseInfo;
import com.onyx.jdread.shop.model.ShopDataBundle;
import com.onyx.jdread.shop.request.cloud.RxRequestSearchBooks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private BookModelBooksResultBean resultBean;
    private List<DataModel> dataModelList = new ArrayList<>();

    private boolean mapToDataModel = false;
    private boolean loadCover = false;

    private String pageSize = PAGE_SIZE_DEFAULT_VALUES;

    public SearchBookListAction(String catId, int currentPage, int sortKey, int sortType, String keyWord, int filter) {
        this.currentPage = currentPage;
        this.catId = catId;
        this.sortType = sortType;
        this.sortKey = sortKey;
        this.keyWord = keyWord;
        this.filter = filter;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public void execute(final ShopDataBundle shopDataBundle, final RxCallback rxCallback) {
        SearchBooksRequestBean requestBean = new SearchBooksRequestBean();
        JDAppBaseInfo appBaseInfo = new JDAppBaseInfo();
        Map<String, String> queryArgs = new HashMap<>();
        queryArgs.put(CloudApiContext.SearchBook.SEARCH_TYPE, SEARCH_TYPE_BOOK_SHOP);
        queryArgs.put(CloudApiContext.SearchBook.CATE_ID, String.valueOf(catId));
        queryArgs.put(CloudApiContext.SearchBook.KEY_WORD, keyWord);
        queryArgs.put(CloudApiContext.SearchBook.FILTER, String.valueOf(filter));
        queryArgs.put(CloudApiContext.SearchBook.SORT, sortKey + "_" + sortType);
        queryArgs.put(CloudApiContext.SearchBook.CURRENT_PAGE, String.valueOf(currentPage));
        queryArgs.put(CloudApiContext.SearchBook.PAGE_SIZE, pageSize);
        appBaseInfo.addRequestParams(queryArgs);
        appBaseInfo.setSign(appBaseInfo.getSignValue(CloudApiContext.BookShopURI.SEARCH_URI));
        requestBean.setAppBaseInfo(appBaseInfo);
        final RxRequestSearchBooks request = new RxRequestSearchBooks();
        request.setRequestBean(requestBean);
        request.setMapToDataModel(mapToDataModel);
        request.setLoadCover(loadCover);
        request.execute(new RxCallback<RxRequestSearchBooks>() {

            @Override
            public void onSubscribe() {
                super.onSubscribe();
                showLoadingDialog(shopDataBundle, R.string.loading);
                invokeSubscribe(rxCallback);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                hideLoadingDialog(shopDataBundle);
                invokeFinally(rxCallback);
            }

            @Override
            public void onNext(RxRequestSearchBooks request) {
                resultBean = request.getResultBean();
                if (mapToDataModel) {
                    dataModelList = request.getDataModelList();
                }
                RxCallback.invokeNext(rxCallback, SearchBookListAction.this);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                RxCallback.invokeError(rxCallback, throwable);
            }

            @Override
            public void onComplete() {
                super.onComplete();
                RxCallback.invokeComplete(rxCallback);
            }
        });
    }

    public void setMapToDataModel(boolean beMapTo) {
        mapToDataModel = beMapTo;
    }

    public void setLoadCover(boolean loadCover) {
        this.loadCover = loadCover;
    }

    public BookModelBooksResultBean getBooksResultBean() {
        return resultBean;
    }

    public List<DataModel> getDataModelList() {
        return dataModelList;
    }
}
