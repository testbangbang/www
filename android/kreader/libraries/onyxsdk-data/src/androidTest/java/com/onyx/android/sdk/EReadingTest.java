package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.Product;
import com.onyx.android.sdk.data.request.cloud.EBookListRequest;
import com.onyx.android.sdk.data.utils.CloudConf;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.data.v1.EBookStoreService;
import com.onyx.android.sdk.data.v1.OnyxBookStoreService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.NetworkUtil;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by suicheng on 2017/5/6.
 */

public class EReadingTest extends ApplicationTestCase<Application> {
    static private final String E_BOOK_HOST = "http://192.168.11.104:8082/";
    static private final String E_BOOK_API = "http://192.168.11.104:8082/api/";

    private static boolean dbInit = false;

    static EBookStoreService eBookService;
    static CloudManager cloudManager;

    public EReadingTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public void init() {
        if (dbInit) {
            return;
        }
        dbInit = true;
        DataManager.init(getContext(), null);
    }

    private final EBookStoreService getEBookService() {
        if (eBookService == null) {
            eBookService = ServiceFactory.getEBookStoreService(getCloudManager().getCloudConf().getApiBase());
        }
        return eBookService;
    }

    private final CloudManager getCloudManager() {
        if (cloudManager == null) {
            cloudManager = new CloudManager();
            CloudConf cloudConf = new CloudConf(E_BOOK_HOST, E_BOOK_API, Constant.DEFAULT_CLOUD_STORAGE);
            cloudManager.setChinaCloudConf(cloudConf);
            cloudManager.setGlobalCloudConf(cloudConf);
        }
        return cloudManager;
    }

    public void testCloudMetadata() throws Exception {
        init();

        //make sure wifi is connected
        runCreatedAtDescQueryArgsRequest();
        runNameAscQueryArgsRequest();

        // test local cache
        NetworkUtil.enableWifi(getContext(), false);
        Thread.sleep(1800);
        runCreatedAtDescQueryArgsRequest();
        runNameAscQueryArgsRequest();
    }

    private void runCreatedAtDescQueryArgsRequest() {
        QueryArgs args = QueryBuilder.allBooksQuery(SortBy.CreationTime, SortOrder.Desc);
        args.limit = 4;
        runQueryArgsRequest(args, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EBookListRequest listRequest = (EBookListRequest) request;
                QueryResult<Metadata> result = listRequest.getProductResult();
                assertTrue(result.count >= CollectionUtils.getSize(result.list));
                Metadata tmp = result.list.get(0);
                for (Metadata metadata : result.list) {
                    assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
                    tmp = metadata;
                }
            }
        });
    }

    private void runNameAscQueryArgsRequest() {
        QueryArgs args = QueryBuilder.allBooksQuery(SortBy.Name, SortOrder.Asc);
        args.limit = 4;
        runQueryArgsRequest(args, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                EBookListRequest listRequest = (EBookListRequest) request;
                QueryResult<Metadata> result = listRequest.getProductResult();
                Metadata tmp = result.list.get(0);
                for (Metadata metadata : result.list) {
                    assertTrue(tmp.getName().compareTo(metadata.getName()) <= 0);
                    tmp = metadata;
                }
            }
        });
    }

    private void runQueryArgsRequest(final QueryArgs queryArgs, final BaseCallback baseCallback) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final EBookListRequest listRequest = new EBookListRequest(queryArgs);
        getCloudManager().submitRequest(getContext(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                QueryResult<Metadata> result = listRequest.getProductResult();
                assertNotNull(result);
                assertTrue(result.count > 0);
                assertTrue(result.count >= CollectionUtils.getSize(result.list));
                assertTrue(CollectionUtils.getSize(result.list) > 0);
                assertTrue(CollectionUtils.getSize(result.list) <= queryArgs.limit);
                BaseCallback.invoke(baseCallback, request, e);
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
