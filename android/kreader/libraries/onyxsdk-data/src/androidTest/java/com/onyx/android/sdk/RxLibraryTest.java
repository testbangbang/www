package com.onyx.android.sdk;

import android.app.Application;
import android.content.Context;
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
        final Library library = generateLibrary();
        QueryArgs args = QueryBuilder.allBooksQuery(SortBy.Author, SortOrder.Asc);
        RxLibraryBuildRequest request = new RxLibraryBuildRequest(dataManager, library, args);
        request.execute(new RxCallback<RxLibraryBuildRequest>() {
            @Override
            public void onNext(RxLibraryBuildRequest rxLibraryBuildRequest) {
                assertLibrary(rxLibraryBuildRequest, library);
                assertTrue(CollectionUtils.isNullOrEmpty(rxLibraryBuildRequest.getBookList()));
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

    public static void assertLibrary(RxLibraryBuildRequest rxLibraryBuildRequest, Library library) {
        Library library1 = rxLibraryBuildRequest.getDataProvider().loadLibrary(library.getIdString());
        assertNotNull(library1);
    }

    public void testRxLibraryBuildRequest2() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();
        dataManager.getRemoteContentProvider().clearLibrary();
        final Library library = generateLibrary();

        QueryArgs args = QueryBuilder.allBooksQuery(SortBy.Author, SortOrder.Asc);
        final int total = TestUtils.randInt(10, 10);
        final String tag = TestUtils.randString();
        dataManager.getRemoteContentProvider().clearMetadata();
        generateMetadata(dataManager, getContext(), total, tag, null, null, null);
        args.tags.add(tag);

        RxLibraryBuildRequest request = new RxLibraryBuildRequest(dataManager, library, args);
        request.execute(new RxCallback<RxLibraryBuildRequest>() {
            @Override
            public void onNext(RxLibraryBuildRequest rxLibraryBuildRequest) {
                assertLibrary(rxLibraryBuildRequest, library);
                List<Metadata> bookList = rxLibraryBuildRequest.getBookList();
                assertFalse(CollectionUtils.isNullOrEmpty(bookList));
                assertEquals(bookList.size(), total);
                for (Metadata metadata : bookList) {
                    assertEquals(metadata.getTags(), tag);
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

    public void testRxLibraryBuildRequest3() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();
        dataManager.getRemoteContentProvider().clearLibrary();
        final Library library = generateLibrary();

        QueryArgs args = QueryBuilder.allBooksQuery(SortBy.Author, SortOrder.Asc);
        final int total = TestUtils.randInt(10, 10);
        final String tag = TestUtils.randString();
        dataManager.getRemoteContentProvider().clearMetadata();
        generateMetadata(dataManager, getContext(), total, tag, null, null, null);

        RxLibraryBuildRequest request = new RxLibraryBuildRequest(dataManager, library, args);
        request.execute(new RxCallback<RxLibraryBuildRequest>() {
            @Override
            public void onNext(RxLibraryBuildRequest rxLibraryBuildRequest) {
                assertLibrary(rxLibraryBuildRequest, library);
                List<Metadata> bookList = rxLibraryBuildRequest.getBookList();
                assertTrue(CollectionUtils.isNullOrEmpty(bookList));
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

    public void testRxLibraryBuildRequest4() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();
        dataManager.getRemoteContentProvider().clearLibrary();
        final Library library = generateLibrary();

        QueryArgs args = QueryBuilder.allBooksQuery(SortBy.Author, SortOrder.Asc);
        final int total = TestUtils.randInt(10, 10);
        final String tag = TestUtils.randString();
        final String title = TestUtils.randString();
        final String author = TestUtils.randString();
        final String series = TestUtils.randString();
        generateMetadata(dataManager, getContext(), total, tag, title, author, series);
        args.tags.add(tag);
        args.title.add(title);
        args.title.add(author);
        args.title.add(series);

        RxLibraryBuildRequest request = new RxLibraryBuildRequest(dataManager, library, args);
        request.execute(new RxCallback<RxLibraryBuildRequest>() {
            @Override
            public void onNext(RxLibraryBuildRequest rxLibraryBuildRequest) {
                assertLibrary(rxLibraryBuildRequest, library);
                List<Metadata> bookList = rxLibraryBuildRequest.getBookList();
                assertFalse(CollectionUtils.isNullOrEmpty(bookList));
                assertEquals(bookList.size(), total);
                for (Metadata metadata : bookList) {
                    assertEquals(metadata.getTags(), tag);
                    assertEquals(metadata.getTitle(), title);
                    assertEquals(metadata.getAuthors(), author);
                    assertEquals(metadata.getSeries(), series);
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

    public static void generateMetadata(DataManager dataManager, Context context, int total, String tag, String title, String author, String series) {
        dataManager.getRemoteContentProvider().clearMetadata();
        for (int i = 0; i < total; i++) {
            Metadata metadata = RxMetadataTest.randomMetadata(RxMetadataTest.testFolder(), true);
            metadata.setTags(tag);
            metadata.setTitle(title);
            metadata.setAuthors(author);
            metadata.setSeries(series);
            dataManager.getRemoteContentProvider().saveMetadata(context, metadata);
        }
    }

    public void testLibraryClearRequest() throws Exception {
        init();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();
        final Library parentLibrary = generateLibrary();
        dataManager.getRemoteContentProvider().addLibrary(parentLibrary);
        int total = TestUtils.randInt(10, 10);
        for (int i = 0; i < total; i++) {
            Library library = generateLibrary();
            library.setParentUniqueId(parentLibrary.getIdString());
            dataManager.getRemoteContentProvider().addLibrary(library);
        }
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, parentLibrary.getIdString());
        assertFalse(CollectionUtils.isNullOrEmpty(libraryList));
        assertEquals(libraryList.size(), total);
        for (Library library : libraryList) {
            assertEquals(library.getParentUniqueId(), parentLibrary.getIdString());
        }

        RxLibraryClearRequest request = new RxLibraryClearRequest(dataManager, parentLibrary);
        request.execute(new RxCallback<RxLibraryClearRequest>() {
            @Override
            public void onNext(RxLibraryClearRequest rxLibraryClearRequest) {
                List<Library> libraryList = new ArrayList<>();
                DataManagerHelper.loadLibraryRecursive(rxLibraryClearRequest.getDataManager(), libraryList, parentLibrary.getIdString());
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

    public Library generateLibrary() {
        final Library library = new Library();
        final String uniqueId = TestUtils.generateUniqueId();
        library.setIdString(uniqueId);
        return library;
    }
}
