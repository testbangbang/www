package com.onyx.android.dr;

import android.test.ApplicationTestCase;

import com.onyx.android.dr.action.ActionChain;
import com.onyx.android.dr.action.AuthTokenAction;
import com.onyx.android.dr.action.CloudLibraryListLoadAction;
import com.onyx.android.dr.device.DeviceConfig;
import com.onyx.android.dr.manager.LeanCloudManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.QueryResult;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.cloud.v2.CloudContentListRequest;
import com.onyx.android.sdk.data.request.cloud.v2.CloudLibraryListLoadRequest;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-6-27.
 */

public class GetContentServerTest extends ApplicationTestCase<DRApplication> {
    public GetContentServerTest() {
        super(DRApplication.class);
    }

    public void testLookUpContentServer() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final AuthTokenAction authTokenAction = new AuthTokenAction();
        final CloudLibraryListLoadAction loadAction = new CloudLibraryListLoadAction("1");
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(authTokenAction);
        actionChain.addAction(loadAction);
        actionChain.execute(DRApplication.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                final List<Library> list = loadAction.getLibraryList();
                assertNotNull(list);
                assertTrue(list.size() > 0);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testLoadCloudLibrary() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CloudManager cloudManager = DRApplication.getCloudStore().getCloudManager();
        final CloudLibraryListLoadRequest loadRequest = new CloudLibraryListLoadRequest("1");
        cloudManager.submitRequest(DRApplication.getInstance(), loadRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                List<Library> libraryList = loadRequest.getLibraryList();
                assertNotNull(libraryList);
                assertTrue(libraryList.size() > 0);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testLoadBooks() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CloudManager cloudManager = DRApplication.getCloudStore().getCloudManager();
        final CloudContentListRequest listRequest = new CloudContentListRequest(null);
        cloudManager.submitRequestToSingle(getContext(), listRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                QueryResult<Metadata> result = listRequest.getProductResult();
                assertNotNull(result);
                assertTrue(result.list.size() > 0);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }
}
