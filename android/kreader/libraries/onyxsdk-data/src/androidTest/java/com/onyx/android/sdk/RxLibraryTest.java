package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.LruCache;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryBuildRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryClearRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryDataCacheClearRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.TestUtils;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-11-4.
 */

public class RxLibraryTest extends ApplicationTestCase<Application> {
    private static boolean dbInit = false;

    public RxLibraryTest() {
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

    public void testRxLibraryBuildRequest() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();
        dataManager.getRemoteContentProvider().clearLibrary();
        final Library library = new Library();
        final String uniqueId = TestUtils.generateUniqueId();
        library.setIdString(uniqueId);
        QueryArgs args = QueryBuilder.allBooksQuery(SortBy.Author, SortOrder.Asc);
        RxLibraryBuildRequest request = new RxLibraryBuildRequest(dataManager, library, args);
        request.execute(new RxCallback<RxLibraryBuildRequest>() {
            @Override
            public void onNext(RxLibraryBuildRequest rxLibraryBuildRequest) {
                Library library1 = rxLibraryBuildRequest.getDataProvider().loadLibrary(uniqueId);
                assertNotNull(library1);
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

    public void testLibraryClearRequest() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();
        final Library parentLibrary = new Library();
        final String parentLibraryID = TestUtils.generateUniqueId();
        parentLibrary.setIdString(parentLibraryID);
        dataManager.getRemoteContentProvider().addLibrary(parentLibrary);
        int total = TestUtils.randInt(1000, 1000);
        for (int i = 0; i < total; i++) {
            Library library = new Library();
            String uniqueId = TestUtils.generateUniqueId();
            library.setIdString(uniqueId);
            library.setParentUniqueId(parentLibraryID);
            dataManager.getRemoteContentProvider().addLibrary(library);
        }
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, parentLibraryID);
        assertFalse(CollectionUtils.isNullOrEmpty(libraryList));
        assertEquals(libraryList.size(), total);

        RxLibraryClearRequest request = new RxLibraryClearRequest(dataManager, parentLibrary);
        request.execute(new RxCallback<RxLibraryClearRequest>() {
            @Override
            public void onNext(RxLibraryClearRequest rxLibraryClearRequest) {
                List<Library> libraryList = new ArrayList<>();
                DataManagerHelper.loadLibraryRecursive(rxLibraryClearRequest.getDataManager(), libraryList, parentLibraryID);
                assertTrue(CollectionUtils.isNullOrEmpty(libraryList));
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

    public void testRxLibraryDataCacheClearRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();
        final CacheManager cacheManager = dataManager.getCacheManager();
        final int total = TestUtils.randInt(100, 100);
        ArrayList<Metadata> list = new ArrayList<>();
        final String metadataCacheKey = TestUtils.randString();
        for (int i = 0; i < total; i++) {
            Metadata metadata = RxMetadataTest.randomMetadata(RxMetadataTest.testFolder(), true);
            list.add(metadata);
        }
        cacheManager.addToMetadataCache(metadataCacheKey, list);
        final LruCache<String, List<Metadata>> lruCache = cacheManager.getMetadataLruCache();
        assertNotNull(lruCache);
        assertEquals(lruCache.get(metadataCacheKey).size(), total);
        RxLibraryDataCacheClearRequest request = new RxLibraryDataCacheClearRequest(dataManager, true, true);
        request.execute(new RxCallback<RxLibraryDataCacheClearRequest>() {
            @Override
            public void onNext(RxLibraryDataCacheClearRequest rxLibraryDataCacheClearRequest) {
                assertNull(rxLibraryDataCacheClearRequest.getDataManager().getCacheManager().getMetadataLruCache().get(metadataCacheKey));
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
