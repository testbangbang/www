package com.onyx.android.sdk;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFilesDiffFromMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxStorageFileListLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.utils.TestUtils.generateRandomFile;

/**
 * Created by hehai on 17-11-1.
 */

public class RxFSRequestTest extends ApplicationTestCase<Application> {
    private static boolean dbInit = false;

    public RxFSRequestTest() {
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

    public void testRxFilesDiffFromMetadata() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        HashSet<String> fileSet = new HashSet<>();
        HashSet<String> metaSet = new HashSet<>();
        fileSet.add("a");
        fileSet.add("b");
        metaSet.add("a");
        RxFilesDiffFromMetadataRequest request = new RxFilesDiffFromMetadataRequest(new DataManager(), fileSet, metaSet);
        request.execute(new RxCallback<RxFilesDiffFromMetadataRequest>() {
            @Override
            public void onNext(RxFilesDiffFromMetadataRequest rxFilesDiffFromMetadataRequest) {
                HashSet<String> diffSet = rxFilesDiffFromMetadataRequest.getDiffSet();
                assertEquals(diffSet.size(), 1);
                assertTrue(diffSet.contains("b"));
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testStorageDataListLoadRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        TestUtils.deleteRecursive(testFolder());
        int total = TestUtils.randInt(7000, 7000);
        for (int i = 0; i < total; i++) {
            generateRandomFile(testFolder(), true);
        }
        RxStorageFileListLoadRequest request = new RxStorageFileListLoadRequest(new DataManager(), new File(testFolder()), new ArrayList<String>());
        request.setSort(SortBy.Name, SortOrder.Asc);
        request.execute(new RxCallback<RxStorageFileListLoadRequest>() {
            @Override
            public void onNext(RxStorageFileListLoadRequest rxStorageFileListLoadRequest) {
                assertTrue(rxStorageFileListLoadRequest.getResultFileList().size() > 0);
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public static String testFolder() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        return path;
    }
}
