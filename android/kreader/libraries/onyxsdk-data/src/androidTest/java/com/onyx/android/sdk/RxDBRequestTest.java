package com.onyx.android.sdk;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.db.ContentDatabase;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxCreateDBRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxExportDataToDBRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxFilesAddToMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxFilesRemoveFromMetadataRequest;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-11-1.
 */

public class RxDBRequestTest extends ApplicationTestCase<Application> {
    private static boolean dbInit = false;

    public RxDBRequestTest() {
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

    public void testCreateDB() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final String exportDBPath = "mnt/sdcard/test.db";
        FileUtils.deleteFile(exportDBPath);
        String currentDbPath = getContext().getDatabasePath(ContentDatabase.NAME).getPath() + ".db";
        DataManager dataManager = new DataManager();
        RxCreateDBRequest request = new RxCreateDBRequest(dataManager, currentDbPath, exportDBPath);
        request.execute(new RxCallback<RxCreateDBRequest>() {
            @Override
            public void onNext(RxCreateDBRequest rxCreateDBRequest) {
                File file = new File(exportDBPath);
                assertTrue(file.exists());
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

    public void testExportDataToDB() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        RxExportDataToDBRequest request = new RxExportDataToDBRequest(new DataManager(), "", "", "", "");
        request.execute(new RxCallback<RxExportDataToDBRequest>() {
            @Override
            public void onNext(RxExportDataToDBRequest rxExportDataToDBRequest) {
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

    public void testRxFilesAddToMetadataRequest() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        File books = new File(Environment.getExternalStorageDirectory(), "Books");
        final File[] files = books.listFiles();
        HashSet<String> set = new HashSet<>();
        for (File file : files) {
            set.add(file.getAbsolutePath());
        }
        RxFilesAddToMetadataRequest request = new RxFilesAddToMetadataRequest(new DataManager(), EnvironmentUtil.getRemovableSDCardCid(), set);
        request.execute(new RxCallback<RxFilesAddToMetadataRequest>() {
            @Override
            public void onNext(RxFilesAddToMetadataRequest rxExportDataToDBRequest) {
                Metadata metadata = rxExportDataToDBRequest.getDataManager().getRemoteContentProvider().findMetadataByPath(getContext(), files[0].getAbsolutePath());
                assertTrue(metadata != null && StringUtils.isNotBlank(metadata.getNativeAbsolutePath()));
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

    public void testRxFilesRemoveFromMetadataRequest() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        File books = new File(Environment.getExternalStorageDirectory(), "Books");
        final File[] files = books.listFiles();
        HashSet<String> set = new HashSet<>();
        for (File file : files) {
            set.add(file.getAbsolutePath());
        }
        RxFilesRemoveFromMetadataRequest request = new RxFilesRemoveFromMetadataRequest(new DataManager(), set);
        Metadata metadata = request.getDataManager().getRemoteContentProvider().findMetadataByPath(getContext(), files[0].getAbsolutePath());
        assertTrue(StringUtils.isNotBlank(metadata.getNativeAbsolutePath()));
        request.execute(new RxCallback<RxFilesRemoveFromMetadataRequest>() {
            @Override
            public void onNext(RxFilesRemoveFromMetadataRequest rxFilesRemoveFromMetadataRequest) {
                Metadata metadata = rxFilesRemoveFromMetadataRequest.getDataManager().getRemoteContentProvider().findMetadataByPath(getContext(), files[0].getAbsolutePath());
                assertFalse(StringUtils.isNotBlank(metadata.getNativeAbsolutePath()));
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
}
