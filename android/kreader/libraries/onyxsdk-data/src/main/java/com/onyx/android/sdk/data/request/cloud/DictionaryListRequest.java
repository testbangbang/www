package com.onyx.android.sdk.data.request.cloud;

import com.alibaba.fastjson.JSON;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.GAdapter;
import com.onyx.android.sdk.data.model.Dictionary;
import com.onyx.android.sdk.data.model.DictionaryQuery;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.utils.CloudUtils;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by zhuzeng on 12/14/15.
 */
public class DictionaryListRequest extends BaseCloudRequest {
    static private final String TAG = DictionaryListRequest.class.getSimpleName();
    private DictionaryQuery dictionaryQuery;
    private ProductResult<Dictionary> productResult;
    private GAdapter adapter;
    private volatile boolean clearCache;
    private volatile boolean cloudOnly;

    public DictionaryListRequest(final DictionaryQuery query, boolean clear, boolean cloud) {
        dictionaryQuery = query;
        clearCache = clear;
        cloudOnly = cloud;
    }

    public final ProductResult<Dictionary> getProductResult() {
        return productResult;
    }

    public final GAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void execute(final CloudManager parent) throws Exception {
        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        } else if (!cloudOnly) {
            fetchFromLocalCache(parent);
        }
        adapter = CloudUtils.adapterFromDictionaryResult(getContext(), productResult, Integer.MAX_VALUE, parent.getCloudConf());
    }

    public void fetchFromLocalCache(final CloudManager parent) throws Exception {
        productResult = new ProductResult<>();
        productResult.list = SQLite.select().from(Dictionary.class).queryList();
        productResult.count = productResult.list.size();
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        Call<ProductResult<Dictionary>> call = ServiceFactory.getDictionaryService(parent.getCloudConf().getApiBase())
                .dictionaryList(JSON.toJSONString(dictionaryQuery));
        Response<ProductResult<Dictionary>> response = executeCall(call);
        if (response.isSuccessful()) {
            productResult = response.body();
            if (isSaveToLocal()) {
                StoreUtils.saveToLocal(productResult, Dictionary.class, clearCache);
            }
        }
    }

}
