package com.onyx.android.dr.presenter;

import com.onyx.android.dr.data.EBookStoreData;
import com.onyx.android.dr.holder.LibraryDataHolder;
import com.onyx.android.dr.interfaces.EBookStoreView;
import com.onyx.android.dr.request.cloud.RequestAddProduct;
import com.onyx.android.dr.request.cloud.RequestCreateOrders;
import com.onyx.android.dr.request.cloud.RequestGetProducts;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.ProductCart;
import com.onyx.android.sdk.data.model.ProductOrder;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.model.v2.ProductRequestBean;
import com.onyx.android.sdk.data.request.cloud.v2.CloudChildLibraryListLoadRequest;
import com.onyx.android.sdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-8-2.
 */

public class EBookStorePresenter {
    private EBookStoreView eBookStoreView;
    private EBookStoreData eBookStoreData;

    public EBookStorePresenter(EBookStoreView eBookStoreView) {
        this.eBookStoreView = eBookStoreView;
        eBookStoreData = new EBookStoreData();
    }

    public void getRootLibraryList(final String parentId) {
        final CloudChildLibraryListLoadRequest req = new CloudChildLibraryListLoadRequest(parentId);
        eBookStoreData.getRootLibraryList(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                getLanguageList();
            }
        });
    }

    public void getLanguageCategoryBooks(LibraryDataHolder dataHolder, String language) {
        eBookStoreData.getLanguageBooks(dataHolder, language, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                eBookStoreView.setBooks(eBookStoreData.getBooks());
            }
        });
    }

    public void getLanguageList() {
        eBookStoreView.setLanguageList(eBookStoreData.getLanguageList());
    }

    public void createOrder(String bookId) {
        List<String> list = new ArrayList<>();
        list.add(bookId);
        ProductRequestBean productRequestBean = new ProductRequestBean(list);
        final RequestCreateOrders req = new RequestCreateOrders(productRequestBean);
        eBookStoreData.createOrder(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ProductOrder<CloudMetadata> order = req.getOrder();
                if (order != null && StringUtils.isNotBlank(order._id)) {
                    eBookStoreView.setOrderId(order._id);
                }
            }
        });
    }

    public void addToCart(String cloudId) {
        List<String> list = new ArrayList<>();
        list.add(cloudId);
        ProductRequestBean productRequestBean = new ProductRequestBean(list);
        final RequestAddProduct req = new RequestAddProduct(productRequestBean);
        eBookStoreData.addToCart(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                ProductCart<CloudMetadata> order = req.getCart();
                if (order != null) {
                    eBookStoreView.setCartCount(order.totalCount);
                }
            }
        });
    }

    public void getCartCount() {
        final RequestGetProducts req = new RequestGetProducts();
        eBookStoreData.getProducts(req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<ProductCart<CloudMetadata>> carts = req.getCarts();
                if (carts != null) {
                    eBookStoreView.setCartCount((int) carts.count);
                }
            }
        });
    }
}
