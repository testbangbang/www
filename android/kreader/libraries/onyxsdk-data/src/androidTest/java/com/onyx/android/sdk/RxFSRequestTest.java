package com.onyx.android.sdk;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileCollectionRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileCopyRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileDeleteRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileSystemDiffRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFileSystemScanRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxFilesDiffFromMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxMediaDeletedFileRemoveRequest;
import com.onyx.android.sdk.data.rxrequest.data.fs.RxStorageFileListLoadRequest;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.util.ArrayList;
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

    public void testRxFileDeleteRequest() throws Exception {
        List<File> sourceList = new ArrayList<>();
        final List<File> deleteList = new ArrayList<>();
        final File targetDir = generateRandomFolder(RxMetadataTest.testFolder());
        for (int i = 0; i < randInt(10, 100); i++) {
            sourceList.add(generateRandomFile(targetDir.getAbsolutePath(), true));
        }

        while (CollectionUtils.isNullOrEmpty(deleteList)) {
            for (File file : sourceList) {
                if (randInt(1, 2) == 1) {
                    deleteList.add(file);
                }
            }
        }

        for (File file : deleteList) {
            assertTrue(file.exists());
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        RxFileDeleteRequest request = new RxFileDeleteRequest(new DataManager(), sourceList);
        request.execute(new RxCallback<RxFileDeleteRequest>() {
            @Override
            public void onNext(RxFileDeleteRequest rxFileDeleteRequest) {
                for (File file : deleteList) {
                    assertFalse(file.exists());
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

            final RxFileSystemScanRequest scanRequest = new RxFileSystemScanRequest(dataManager, sid, list, true);
            scanRequest.execute(new RxCallback<RxFileSystemScanRequest>() {
                @Override
                public void onNext(RxFileSystemScanRequest rxFileSystemScanRequest) {
                    assertTrue(scanRequest.getResult().size() == total);
                    snapshot.addAll(scanRequest.getResult());
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    assertNull(throwable);
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

            final RxFileSystemScanRequest scanRequest = new RxFileSystemScanRequest(dataManager, sid, list, false);
            scanRequest.execute(new RxCallback<RxFileSystemScanRequest>() {
                @Override
                public void onNext(RxFileSystemScanRequest rxFileSystemScanRequest) {
                    assertTrue(scanRequest.getResult().size() == snapshot.size());
                    assertTrue(scanRequest.getResult().equals(snapshot));
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    assertNull(throwable);
                    countDownLatch.countDown();
                }
            });

            awaitCountDownLatch(countDownLatch);
        }

        {
            // calculate diff.
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final RxFileSystemDiffRequest diffRequest = new RxFileSystemDiffRequest(dataManager, sid, snapshot);
            diffRequest.execute(new RxCallback<RxFileSystemDiffRequest>() {
                @Override
                public void onNext(RxFileSystemDiffRequest rxFileSystemDiffRequest) {
                    assertTrue(diffRequest.getAdded().equals(addedSet));
                    assertTrue(diffRequest.getRemoved().equals(removeSet));
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    assertNull(throwable);
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
        }
    }

    public void testRxMediaDeletedFileRemoveRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final ContentResolver contentResolver = getContext().getContentResolver();
        clearDocFolder();
        List<String> fileList = new ArrayList<>();
        for (int i = 0; i < randInt(10, 50); i++) {
            File mp3 = TestUtils.generateRandomFile(docFolder(), "mp3");
            fileList.add(mp3.getAbsolutePath());
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, mp3.getName());
            contentValues.put(MediaStore.Audio.Media.DATA, mp3.getAbsolutePath());
            contentValues.put(MediaStore.Audio.Media.SIZE, mp3.length());
            contentValues.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3");
            contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, contentValues);
        }

        for (String file : fileList) {
            FileUtils.deleteFile(file);
        }

        List<String> targetPathList = new ArrayList<>();
        ContentResolver resolver = getContext().getContentResolver();
        for (String s : fileList) {
            Cursor cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + "=?", new String[]{s}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    targetPathList.add(path);
                }
            }
        }

        assertEquals(fileList, targetPathList);

        RxMediaDeletedFileRemoveRequest request = new RxMediaDeletedFileRemoveRequest(new DataManager(), targetPathList);
        RxMediaDeletedFileRemoveRequest.setAppContext(getContext());
        request.execute(new RxCallback<RxMediaDeletedFileRemoveRequest>() {
            @Override
            public void onNext(RxMediaDeletedFileRemoveRequest removeRequest) {

                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
        for (String s1 : fileList) {
            Cursor cursor1 = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, MediaStore.Audio.Media.DATA + "=?", new String[]{s1}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            assertTrue(cursor1 == null || !cursor1.moveToNext());
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

    private void clearDocFolder() {
        File file = new File(docFolder());
        if (file.exists()) {
            FileUtils.purgeDirectory(new File(docFolder()));
        } else {
            FileUtils.mkdirs(docFolder());
        }
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
}
