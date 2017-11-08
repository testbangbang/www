package com.onyx.android.sdk;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxApplicationListLoadRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileAutoSuffixNameRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileCollectionRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileCopyRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFilesDiffFromMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxStorageFileListLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.utils.TestUtils.defaultContentTypes;
import static com.onyx.android.sdk.utils.TestUtils.generateRandomFile;
import static com.onyx.android.sdk.utils.TestUtils.generateRandomFolder;
import static com.onyx.android.sdk.utils.TestUtils.randInt;
import static com.onyx.android.sdk.utils.TestUtils.randString;

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
        final HashSet<String> otherSet = new HashSet<>();
        for (int i = 0; i < randInt(50, 100); i++) {
            String s = randString();
            int type = randInt(1, 2);
            fileSet.add(s);
            if (type == 1) {
                fileSet.add(s);
            } else {
                otherSet.add(s);
            }
        }
        RxFilesDiffFromMetadataRequest request = new RxFilesDiffFromMetadataRequest(new DataManager(), fileSet, metaSet);
        request.execute(new RxCallback<RxFilesDiffFromMetadataRequest>() {
            @Override
            public void onNext(RxFilesDiffFromMetadataRequest rxFilesDiffFromMetadataRequest) {
                HashSet<String> diffSet = rxFilesDiffFromMetadataRequest.getDiffSet();
                assertEquals(diffSet.size(), otherSet.size());
                for (String s : diffSet) {
                    assertTrue(otherSet.contains(s));
                }
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
        TestUtils.deleteRecursive(RxMetadataTest.testFolder());
        int total = TestUtils.randInt(7000, 7000);
        for (int i = 0; i < total; i++) {
            generateRandomFile(RxMetadataTest.testFolder(), true);
        }
        RxStorageFileListLoadRequest request = new RxStorageFileListLoadRequest(new DataManager(), new File(RxMetadataTest.testFolder()), new ArrayList<String>());
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

    public void testRxFileCollectionRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<String> dirList = new ArrayList<>();
        final List<String> targetList = new ArrayList<>();
        final Set<String> extFilterList = new HashSet<>();
        for (int i = 0; i < randInt(1, defaultContentTypes().size()); i++) {
            String type = TestUtils.randomType();
            extFilterList.add(type);
        }

        TestUtils.deleteRecursive(RxMetadataTest.testFolder());
        String parentFile = RxMetadataTest.testFolder();
        List<File> files = generateRandomDirList(parentFile);
        for (File file1 : files) {
            dirList.add(file1.getAbsolutePath());
            for (int i = 0; i < randInt(10, 20); i++) {
                File file2 = generateRandomFile(file1.getAbsolutePath(), true);
                if (extFilterList.contains(FileUtils.getFileExtension(file2))) {
                    targetList.add(file2.getAbsolutePath());
                }
            }
        }

        final RxFileCollectionRequest request = new RxFileCollectionRequest(new DataManager(), dirList, extFilterList);
        request.execute(new RxCallback<RxFileCollectionRequest>() {
            @Override
            public void onNext(RxFileCollectionRequest collectionRequest) {
                List<String> resultFileList = collectionRequest.getResultFileList();
                assertFalse(CollectionUtils.isNullOrEmpty(resultFileList));
                assertEquals(resultFileList.size(), targetList.size());
                for (String s : resultFileList) {
                    String fileExtension = FileUtils.getFileExtension(s);
                    assertTrue(extFilterList.contains(fileExtension));
                    assertTrue(targetList.contains(s));
                }
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

    public void testRxFileCopyRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final File targetDir = generateRandomFolder(RxMetadataTest.testFolder());
        final File sourceDir = generateRandomFolder(RxMetadataTest.testFolder());
        List<File> sourceFiles = new ArrayList<>();
        for (int i = 0; i < randInt(100, 200); i++) {
            File file = generateRandomFile(sourceDir.getAbsolutePath(), true);
            sourceFiles.add(file);
        }

        boolean isCut = 1 == randInt(1, 2);
        int sourceSize = sourceFiles.size();

        RxFileCopyRequest request = new RxFileCopyRequest(new DataManager(), sourceFiles, targetDir, isCut);
        request.execute(new RxCallback<RxFileCopyRequest>() {
            @Override
            public void onNext(RxFileCopyRequest rxFileCopyRequest) {
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
        File[] files = targetDir.listFiles();
        assertEquals(sourceSize, files.length);
        if (isCut) {
            File[] files1 = sourceDir.listFiles();
            assertNotNull(files1);
        } else {
            for (File sourceFile : sourceFiles) {
                boolean have = false;
                String sourceMd5 = FileUtils.computeMD5(sourceFile);
                for (File file : files) {
                    String targetMd5 = FileUtils.computeMD5(file);
                    if (sourceMd5.equals(targetMd5)) {
                        have = true;
                    }
                }
                assertTrue(have);
            }
        }
    }

    private List<File> generateRandomDirList(String parentFile) {
        List<File> dirList = new ArrayList<>();
        File file = new File(parentFile);
        for (int i = 0; i < randInt(5, 10); i++) {
            File file1 = generateRandomFolder(file.getAbsolutePath());
            dirList.add(file1);
        }
        return dirList;
    }
}
