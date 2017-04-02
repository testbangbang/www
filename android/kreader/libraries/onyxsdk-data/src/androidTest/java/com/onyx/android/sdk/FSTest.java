package com.onyx.android.sdk;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.request.data.fs.FileSystemScanRequest;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.utils.TestUtils.deleteRecursive;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FSTest extends ApplicationTestCase<Application> {

    private static boolean dbInit = false;

    public FSTest() {
        super(Application.class);
    }

    private void clearTestFolder() {
        deleteRecursive(testFolder());
    }

    public static String testFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    }

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void test00FSRequest() {
        clearTestFolder();
        final int total = TestUtils.randInt(100, 500);
        for(int i = 0; i < total; ++i) {
            TestUtils.generateRandomFile(testFolder(), true);
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        DataManager dataManager = new DataManager();
        final List<String> list = new ArrayList<>();
        list.add(testFolder());

        final FileSystemScanRequest scanRequest = new FileSystemScanRequest(list);
        dataManager.submit(getContext(), scanRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertTrue(scanRequest.getResult().size() == total);
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }


}
