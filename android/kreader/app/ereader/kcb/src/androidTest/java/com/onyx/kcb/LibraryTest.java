package com.onyx.kcb;

import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.device.EnvironmentUtil;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.onyx.kcb.action.RxFileSystemScanAction;
import com.onyx.kcb.action.RxMetadataLoadAction;
import com.onyx.kcb.holder.LibraryDataHolder;
import com.onyx.kcb.model.LibraryViewDataModel;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-11-21.
 */

public class LibraryTest extends ApplicationTestCase<KCBApplication> {
    public LibraryTest() {
        super(KCBApplication.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testMetadataLoadAction() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        Debug.setDebug(true);
        final LibraryDataHolder dataHolder = new LibraryDataHolder(getContext());
        clearData(dataHolder.getDataManager());
        boolean isFlash = true;
        FileUtils.deleteFile(new File(getBookDirList(isFlash)), true);
        final List<DataModel> fileList = new ArrayList<>();
        for (int i = 0; i < TestUtils.randInt(500, 1000); i++) {
            File file = TestUtils.generateRandomFile(getBookDirList(isFlash), true);
            DataModel dataModel = new DataModel(dataHolder.getEventBus());
            dataModel.absolutePath.set(file.getAbsolutePath());
            dataModel.title.set(file.getName());
            fileList.add(dataModel);
        }
        final Benchmark benchmark = new Benchmark();
        RxFileSystemScanAction action = new RxFileSystemScanAction(RxFileSystemScanAction.MMC_STORAGE_ID, isFlash);
        action.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                benchmark.report("testMetadataLoadAction scan" + " count:" + fileList.size());
                benchmark.restart();
                countDownLatch.countDown();
                loadData(fileList, dataHolder, new RxCallback() {
                    @Override
                    public void onNext(Object o) {
                        benchmark.report("testMetadataLoadAction loadData" + " count:" + fileList.size());
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                        assertNull(throwable);
                        countDownLatch.countDown();
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                countDownLatch.countDown();
            }
        });

        countDownLatch.await();
        clearData(dataHolder.getDataManager());
        FileUtils.deleteFile(new File(getBookDirList(true)), true);
    }

    private void loadData(final List<DataModel> fileList, LibraryDataHolder dataHolder, final RxCallback rxCallback) {
        final LibraryViewDataModel libraryViewDataModel = LibraryViewDataModel.create(EventBus.getDefault(), Integer.MAX_VALUE, Integer.MAX_VALUE);
        QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.Name, SortOrder.Asc);
        RxMetadataLoadAction rxMetadataLoadAction = new RxMetadataLoadAction(libraryViewDataModel, queryArgs, false);
        rxMetadataLoadAction.execute(dataHolder, new RxCallback() {
            @Override
            public void onNext(Object o) {
                assertListEqual(libraryViewDataModel.items, fileList);
                rxCallback.onNext(o);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                assertNull(throwable);
                rxCallback.onError(throwable);
            }
        });
    }

    public static void assertListEqual(List<DataModel> resultList, List<DataModel> targetList) {
        assertFalse(CollectionUtils.isNullOrEmpty(resultList));
        assertFalse(CollectionUtils.isNullOrEmpty(targetList));
        assertEquals(resultList.size(), targetList.size());
        Iterator<DataModel> iterator = resultList.iterator();
        while (iterator.hasNext()) {
            DataModel next = iterator.next();
            Iterator<DataModel> iterator1 = targetList.iterator();
            while (iterator1.hasNext()) {
                DataModel next1 = iterator1.next();
                if (next1.absolutePath.get().equals(next.absolutePath.get())) {
                    iterator1.remove();
                }
            }
            iterator.remove();
        }

        assertTrue(CollectionUtils.isNullOrEmpty(resultList));
    }

    private void clearData(DataManager dataManager) {
        dataManager.getRemoteContentProvider().clearLibrary();
        dataManager.getRemoteContentProvider().clearThumbnails();
        dataManager.getRemoteContentProvider().clearMetadata();
        dataManager.getRemoteContentProvider().clearMetadataCollection();
    }

    private String getBookDirList(boolean isFlash) {
        String rootDir;
        if (isFlash) {
            rootDir = EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath();
        } else {
            rootDir = EnvironmentUtil.getRemovableSDCardDirectory().getAbsolutePath();
        }
        return rootDir;
    }
}
