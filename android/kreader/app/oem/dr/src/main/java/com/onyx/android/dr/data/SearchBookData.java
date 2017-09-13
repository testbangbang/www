package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.bean.ProductBean;
import com.onyx.android.dr.request.cloud.RequestSearchProduct;
import com.onyx.android.dr.request.local.RequestSearchHistoryDelete;
import com.onyx.android.dr.request.local.RequestSearchHistoryInsert;
import com.onyx.android.dr.request.local.RequestSearchHistoryQuery;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.v2.CloudMetadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-8-15.
 */

public class SearchBookData {
    private List<String> hints = new ArrayList<>();
    private List<ProductBean> localList = new ArrayList<>();
    private List<ProductBean> cloudList = new ArrayList<>();
    private List<ProductBean> result = new ArrayList<>();

    public void searchLocalBook(final CloudContentListRequest req, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<Metadata> productResult = req.getProductResult();
                hints.clear();
                localList.clear();
                if (productResult != null && !CollectionUtils.isNullOrEmpty(productResult.list)) {
                    for (Metadata metadata : productResult.list) {
                        ProductBean productBean = new ProductBean(metadata);
                        if (localList.isEmpty()) {
                            productBean.setFirst(true);
                        }
                        hints.add(metadata.getName());
                        localList.add(productBean);
                    }
                }
                invoke(baseCallback, request, e);
            }
        });
    }

    public List<String> getHints() {
        return hints;
    }


    public void getHistory(final RequestSearchHistoryQuery req, final BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void clearHistory(final RequestSearchHistoryDelete req, final BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void insertHistory(final RequestSearchHistoryInsert req, final BaseCallback callback) {
        submitRequest(req, callback);
    }

    public void submitRequest(final BaseDataRequest req, final BaseCallback callBack) {
        DRApplication.getDataManager().submit(DRApplication.getInstance(), req, callBack);
    }

    public void searchCloudBook(final RequestSearchProduct reqCloud, final BaseCallback baseCallback) {
        DRApplication.getCloudStore().submitRequest(DRApplication.getInstance(), reqCloud, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                QueryResult<CloudMetadata> productResult = reqCloud.getResult();
                hints.clear();
                cloudList.clear();
                if (productResult != null && !CollectionUtils.isNullOrEmpty(productResult.list)) {
                    for (Metadata metadata : productResult.list) {
                        ProductBean productBean = new ProductBean(metadata);
                        if (cloudList.isEmpty()) {
                            productBean.setFirst(true);
                        }
                        hints.add(metadata.getName());
                        cloudList.add(productBean);
                    }
                }
                invoke(baseCallback, request, e);
            }
        });
    }

    public List<ProductBean> getResult() {
        result.clear();
        result.addAll(localList);
        result.addAll(cloudList);
        return result;
    }
}
