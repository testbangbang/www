package com.onyx.android.sdk.data.request.cloud;

import android.content.Context;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.model.Link;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.model.PushProduct;
import com.onyx.android.sdk.data.model.PushRecord;
import com.onyx.android.sdk.data.model.PushProduct_Table;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/10/14.
 */

public class PushRecordListRequest extends BaseCloudRequest {

    private boolean cloudOnly = false;
    private boolean sync = false;
    private List<PushRecord> recordList = new ArrayList<>();
    private List<PushProduct> productList = new ArrayList<>();
    private List<Link> linkList = new ArrayList<>();

    /**
     * @param cloud false use Local database.
     * @param sync  sync data to local from Cloud when cloud is true
     */
    public PushRecordListRequest(boolean cloud, boolean sync) {
        this.sync = sync;
        this.cloudOnly = cloud;
    }

    public List<PushProduct> getProductList() {
        return productList;
    }

    public List<Link> getLinkList() {
        return linkList;
    }

    public List<PushRecord> getRecordList() {
        return recordList;
    }

    @Override
    public void execute(CloudManager parent) throws Exception {
        if (!cloudOnly) {
            fetchFromLocalCache(parent);
            return;
        }

        if (CloudManager.isWifiConnected(getContext())) {
            fetchFromCloud(parent);
        } else {
            fetchFromLocalCache(parent);
        }
    }

    public void fetchFromLocalCache(final CloudManager parent) throws Exception {
        productList = loadLocalData();
        processProductFileExist(getContext(), productList);
        parseLinkList(productList, parent.getCloudConf().getCloudStorage());
    }

    public void fetchFromCloud(final CloudManager parent) throws Exception {
        Response<ProductResult<PushRecord>> response = executeCall(ServiceFactory.getPushService(parent.getCloudConf().getApiBase())
                .pushRecordList(getAccountSessionToken()));
        if (response.isSuccessful()) {
            ProductResult<PushRecord> result = response.body();
            if (!StoreUtils.isEmpty(result)) {
                recordList = result.list;
                for (PushRecord pushRecord : result.list) {
                    PushProduct product = pushRecord.parsePushProduct();
                    if (product != null) {
                        productList.add(product);
                    }
                }
                if (sync && productList.size() > 0) {
                    syncDataToLocal(productList);
                }
                processProductFileExist(getContext(), productList);
                parseLinkList(productList, parent.getCloudConf().getCloudStorage());
            }
        }
    }

    /**
     * 这两个for循环会随着内容的增多，耗时会成倍增加，后面优化
     */
    private void syncDataToLocal(List<PushProduct> cloudList) {
        List<PushProduct> localList = loadLocalData();
        if (!CollectionUtils.isNullOrEmpty(localList)) {
            for (PushProduct local : localList) {
                for (PushProduct cloud : cloudList) {
                    if (StringUtils.isNotBlank(local.getGuid()) && local.getGuid().equals(cloud.getGuid())) {
                        cloud.setIdString(local.getIdString());
                        break;
                    }
                }
            }
        }
        StoreUtils.saveToLocal(cloudList, PushProduct.class, true);
    }

    private void processProductFileExist(Context context, List<PushProduct> list) {
        boolean result;
        for (PushProduct product : list) {
            result = false;
            Metadata meta = DataManagerHelper.getMetadataByCloudReference(context, product.getGuid());
            if (meta == null) {
                if (StringUtils.isNotBlank(product.getIdString())) {
                    meta = DataManagerHelper.getMetadataByHashTag(context, product.getIdString());
                }
            }
            if (meta != null) {
                File file = new File(meta.getNativeAbsolutePath());
                result = file.exists();
            }
            product.isFiLeExist = result;
        }
    }

    private void parseLinkList(List<PushProduct> list, String cloudStorage) {
        if (CollectionUtils.isNullOrEmpty(list)) {
            return;
        }
        for (PushProduct product : list) {
            Link link = product.getFirstDownloadLink();
            if (link == null) {
                link = new Link();
            }
            linkList.add(link);
        }
    }

    private List<PushProduct> loadLocalData() {
        return StoreUtils.queryDataList(PushProduct.class, OrderBy.fromProperty(PushProduct_Table.createdAt).descending());
    }
}
