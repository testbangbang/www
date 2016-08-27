package com.onyx.cloud.v1;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.cloud.CloudManager;
import com.onyx.cloud.Constant;
import com.onyx.cloud.OnyxDownloadManager;
import com.onyx.cloud.service.v1.OnyxFileDownloadService;
import com.onyx.cloud.service.v1.ServiceFactory;
import com.onyx.cloud.store.request.CloudFileRequest;
import com.onyx.cloud.store.request.ParseCoverRequest;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by suicheng on 2016/8/16.
 */
public class DownloadTest extends ApplicationTestCase<Application> {
    private static final String SDCARD_ROOT_DIR = "/mnt/sdcard";
    private static final String TEST_FILE_PATH = SDCARD_ROOT_DIR + "/Download/testFileDownloader.jpg";
    //2.81M
    private static final String TEST_URL = "http://7xjww9.com1.z0.glb.clouddn.com/Hopetoun_falls.jpg";

    private static final long TEST_FILE_SIZE = 2800000;

    public DownloadTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public void testDownloadService() throws Exception {
        OnyxFileDownloadService service = ServiceFactory.getFileDownloadService(Constant.CN_API_BASE);
        Call<ResponseBody> call = service.fileDownload(TEST_URL);
        Response<ResponseBody> response = call.execute();
        assertNotNull(response);
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertTrue(response.body().contentLength() > TEST_FILE_SIZE);
    }

    public void testCloudFileRequest() throws Throwable {
        final Context context = getApplication().getApplicationContext();
        assertNotNull(context);
        final CountDownLatch latch = new CountDownLatch(1);
        final String tag = UUID.randomUUID().toString();
        final OnyxDownloadManager fileDownloadManager = OnyxDownloadManager.getInstance(context);
        final CloudFileRequest fileDownloadRequest = new CloudFileRequest(TEST_FILE_PATH, TEST_URL, tag);
        fileDownloadManager.download(fileDownloadRequest, new BaseCallback() {
            @Override
            public void progress(BaseRequest request, ProgressInfo info) {
            }

            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNotNull(e);
                onFileDownloadFinished(context, fileDownloadRequest.path, latch);
            }
        });
        waitLatch(latch);
    }

    private void onFileDownloadFinished(Context context, String path, final CountDownLatch latch) {
        CloudManager cloudManager = new CloudManager();
        final ParseCoverRequest coverRequest = new ParseCoverRequest(path);
        cloudManager.submitRequest(context, coverRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                Bitmap bitmap = coverRequest.getBitmap();
                assertNotNull(bitmap);
                latch.countDown();
            }
        });
    }

    private void waitLatch(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}