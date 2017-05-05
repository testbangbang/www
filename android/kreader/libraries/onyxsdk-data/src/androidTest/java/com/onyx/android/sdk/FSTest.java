package com.onyx.android.sdk;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.fs.FileSystemDiffRequest;
import com.onyx.android.sdk.data.request.data.fs.FileSystemScanRequest;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.utils.TestUtils.deleteRecursive;

/**
 * Created by zhuzeng on 02/04/2017.
 */

public class FSTest extends ApplicationTestCase<Application> {


    public FSTest() {
        super(Application.class);
    }

    private void clearDocFolder() {
        FileUtils.purgeDirectory(new File(docFolder()));
        FileUtils.mkdirs(docFolder());
    }

    public static String docFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
    }

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testFSScanRequest() {
        final String sid = "tf";
        DataManager dataManager = new DataManager();
        final HashSet<String> snapshot = new HashSet<>();
        final List<String> origin = new ArrayList<>();
        {
            clearDocFolder();

            final int total = TestUtils.randInt(100, 500);
            for (int i = 0; i < total; ++i) {
                final File file = TestUtils.generateRandomFile(docFolder(), true);
                origin.add(file.getAbsolutePath());
            }

            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final List<String> list = new ArrayList<>();
            list.add(docFolder());

            final FileSystemScanRequest scanRequest = new FileSystemScanRequest(sid, list, true);
            dataManager.submit(getContext(), scanRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    assertTrue(scanRequest.getResult().size() == total);
                    snapshot.addAll(scanRequest.getResult());
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
        }

        final HashSet<String> addedSet = new HashSet<>();
        final HashSet<String> removeSet = new HashSet<>();
        {
            // add some files
            final int addedCount = TestUtils.randInt(100, 500);

            for (int i = 0; i < addedCount; ++i) {
                final File file = TestUtils.generateRandomFile(docFolder(), true);
                addedSet.add(file.getAbsolutePath());
                snapshot.add(file.getAbsolutePath());
            }

            // remove some files.
            final int removedCount = TestUtils.randInt(10, origin.size());
            for (int i = 0; i < removedCount; ++i) {
                removeSet.add(origin.get(i));
                snapshot.remove(origin.get(i));
                FileUtils.deleteFile(origin.get(i));
            }

            // rescan
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final List<String> list = new ArrayList<>();
            list.add(docFolder());


            final FileSystemScanRequest scanRequest = new FileSystemScanRequest(sid, list, false);
            dataManager.submit(getContext(), scanRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    assertTrue(scanRequest.getResult().size() == snapshot.size());
                    assertTrue(scanRequest.getResult().equals(snapshot));
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
        }

        {
            // calculate diff.
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final FileSystemDiffRequest diffRequest = new FileSystemDiffRequest(sid, snapshot);
            dataManager.submit(getContext(), diffRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    assertTrue(diffRequest.getAdded().equals(addedSet));
                    assertTrue(diffRequest.getRemoved().equals(removeSet));
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
        }
    }



}
