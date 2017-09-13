package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.BookDetailData;
import com.onyx.android.dr.interfaces.BookDetailView;
import com.onyx.android.dr.request.cloud.RequestAddProduct;
import com.onyx.android.dr.request.cloud.RequestCreateOrders;
import com.onyx.android.dr.request.cloud.RequestGetBookDetail;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.ProductRequestBean;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-9-6.
 */

public class BookDetailPresenter {
    private BookDetailView bookDetailView;
    private BookDetailData bookDetailData;

    public BookDetailPresenter(BookDetailView bookDetailView) {
        this.bookDetailView = bookDetailView;
        bookDetailData = new BookDetailData();
    }

    public void loadBookDetail(String bookId) {
        final RequestGetBookDetail req = new RequestGetBookDetail(bookId);
        bookDetailData.loadBookDetail(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                CloudMetadata cloudMetadata = req.getCloudMetadata();
                if (cloudMetadata != null) {
                    bookDetailView.setBookDetail(req.getCloudMetadata());
                }
            }
        });
    }

    public void createOrder(String bookId) {
        List<String> list = new ArrayList<>();
        list.add(bookId);
        ProductRequestBean productRequestBean = new ProductRequestBean(list);
        final RequestCreateOrders req = new RequestCreateOrders(productRequestBean);
        bookDetailData.createOrder(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ProductOrder<CloudMetadata> order = req.getOrder();
                if (order != null && StringUtils.isNotBlank(order._id)) {
                    bookDetailView.setOrderId(order._id);
                }
            }
        });
    }

    public void addToCart(String cloudId) {
        List<String> list = new ArrayList<>();
        list.add(cloudId);
        ProductRequestBean productRequestBean = new ProductRequestBean(list);
        final RequestAddProduct req = new RequestAddProduct(productRequestBean);
        bookDetailData.addToCart(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ProductCart<CloudMetadata> order = req.getCart();
                if (order != null) {
                    bookDetailView.setCartCount(order.totalCount);
                }
            }
        });
    }
}
