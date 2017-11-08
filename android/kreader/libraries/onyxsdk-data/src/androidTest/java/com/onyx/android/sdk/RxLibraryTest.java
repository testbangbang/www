package com.onyx.android.sdk;

import android.app.Application;
import android.content.Context;
import android.test.ApplicationTestCase;
import android.util.Log;
import android.util.LruCache;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.manager.CacheManager;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.LibraryTableOfContentEntry;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.rxrequest.data.db.RxBaseDBRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryBuildRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryClearRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryDataCacheClearRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryDeleteRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryGotoRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryMoveToRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryTableOfContentLoadRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxModifyLibraryRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by hehai on 17-11-4.
 */

public class RxLibraryTest extends ApplicationTestCase<Application> {
    private static boolean dbInit = false;
    private static int maxLevel = 2;

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
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = getDataManager();
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

    public static void assertLibrary(RxBaseDBRequest rxBaseDBRequest, Library library) {
        Library library1 = rxBaseDBRequest.getDataProvider().loadLibrary(library.getIdString());
        assertNotNull(library1);
    }

    public void testRxLibraryBuildRequest2() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = getDataManager();
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
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = getDataManager();
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
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = getDataManager();
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

    public static List<Metadata> generateMetadata(DataManager dataManager, Context context, int total, String tag, String title, String author, String series) {
        dataManager.getRemoteContentProvider().clearMetadata();
        List<Metadata> list = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            Metadata metadata = RxMetadataTest.randomMetadata(RxMetadataTest.testFolder(), true);
            metadata.setTags(tag);
            metadata.setTitle(title);
            metadata.setAuthors(author);
            metadata.setSeries(series);
            dataManager.getRemoteContentProvider().saveMetadata(context, metadata);
            list.add(metadata);
        }
        return list;
    }

    public void testLibraryClearRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = getDataManager();
        final Library parentLibrary = generateLibrary();
        dataManager.getRemoteContentProvider().addLibrary(parentLibrary);
        int total = TestUtils.randInt(10, 100);
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
        DataManager dataManager = getDataManager();
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

    public static Library generateLibrary() {
        final Library library = new Library();
        final String uniqueId = TestUtils.generateUniqueId();
        library.setIdString(uniqueId);
        return library;
    }

    public void testRxLibraryDeleteRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        DataManager dataManager = getDataManager();
        final Library library = generateLibrary();
        dataManager.getRemoteContentProvider().addLibrary(library);
        Library library1 = dataManager.getRemoteContentProvider().loadLibrary(library.getIdString());
        assertNotNull(library1);

        RxLibraryDeleteRequest request = new RxLibraryDeleteRequest(dataManager, library);
        request.execute(new RxCallback<RxLibraryDeleteRequest>() {
            @Override
            public void onNext(RxLibraryDeleteRequest rxLibraryDeleteRequest) {
                Library library2 = rxLibraryDeleteRequest.getDataProvider().loadLibrary(library.getIdString());
                assertNull(library2);
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

    public void testRxLibraryGotoRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = getDataManager();
        final Library parentLibrary = generateLibrary();
        final int layers = TestUtils.randInt(1, 10);
        final int childCount = TestUtils.randInt(1, 10);
        final String parentTag = TestUtils.randString();
        final String childTag = TestUtils.randString();
        final Library library = generateChildLibraryRecursive(dataManager.getRemoteContentProvider(), parentLibrary, parentTag, layers);
        for (int i = 0; i < childCount; i++) {
            Library childLibrary = generateLibrary();
            childLibrary.setDescription(childTag);
            childLibrary.setParentUniqueId(library.getIdString());
            dataManager.getRemoteContentProvider().addLibrary(childLibrary);
        }
        RxLibraryGotoRequest request = new RxLibraryGotoRequest(dataManager, library);
        request.setLoadParentLibrary(true);
        request.execute(new RxCallback<RxLibraryGotoRequest>() {
            @Override
            public void onNext(RxLibraryGotoRequest rxLibraryGotoRequest) {
                List<Library> subLibraryList = rxLibraryGotoRequest.getSubLibraryList();
                assertFalse(CollectionUtils.isNullOrEmpty(subLibraryList));
                assertEquals(subLibraryList.size(), childCount);
                for (Library library1 : subLibraryList) {
                    assertEquals(library1.getDescription(), childTag);
                }
                List<Library> parentLibraryList = rxLibraryGotoRequest.getParentLibraryList();
                assertFalse(CollectionUtils.isNullOrEmpty(parentLibraryList));
                assertEquals(parentLibraryList.size(), layers);
                for (Library library1 : parentLibraryList) {
                    assertEquals(library1.getDescription(), parentTag);
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

    private Library generateChildLibraryRecursive(DataProviderBase dataProvider, Library parentLibrary, String parentTag, int layers) {
        parentLibrary.setDescription(parentTag);
        dataProvider.addLibrary(parentLibrary);
        Library library = null;
        String parentUniqueId = parentLibrary.getIdString();
        while (layers > 0) {
            library = generateLibrary();
            library.setParentUniqueId(parentUniqueId);
            library.setDescription(parentTag);
            dataProvider.addLibrary(library);
            parentUniqueId = library.getIdString();
            layers--;
        }
        return library;
    }

    public void testRxLibraryMoveToRequest() throws Exception {
        DataManager dataManager = getDataManager();
        List<Library> list = new ArrayList<>();
        for (int i = 0; i < TestUtils.randInt(5, 10); i++) {
            Library library = generateLibrary();
            list.add(library);
            dataManager.getRemoteContentProvider().addLibrary(library);
        }
        int total = TestUtils.randInt(10, 50);
        final String tag = TestUtils.randString();
        final String title = TestUtils.randString();
        final String author = TestUtils.randString();
        final String series = TestUtils.randString();

        int from = TestUtils.randInt(0, list.size() - 1);
        for (int i = 0; i < 5; i++) {
            final CountDownLatch countDownLatch = new CountDownLatch(2);
            int to = getRandomInt(list.size() - 1, from);
            final List<Metadata> metadataList = generateMetadata(dataManager, getContext(), total, tag, title, author, series);
            final RxLibraryMoveToRequest request = new RxLibraryMoveToRequest(dataManager, list.get(from), list.get(to), metadataList);

            request.execute(new RxCallback<RxLibraryMoveToRequest>() {
                @Override
                public void onNext(RxLibraryMoveToRequest rxLibraryMoveToRequest) {
                    countDownLatch.countDown();
                }

                @Override
                public void onError(Throwable throwable) {
                    super.onError(throwable);
                    assertNull(throwable);
                    countDownLatch.countDown();
                }
            });
            from = to;

            QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.Author, SortOrder.Asc);
            queryArgs.libraryUniqueId = list.get(to).getIdString();
            final RxMetadataRequest rxMetadataRequest = new RxMetadataRequest(dataManager, queryArgs);
            rxMetadataRequest.execute(new RxCallback<RxMetadataRequest>() {
                @Override
                public void onNext(RxMetadataRequest rxMetadataRequest1) {
                    List<Metadata> list1 = rxMetadataRequest1.getList();
                    assertFalse(CollectionUtils.isNullOrEmpty(list1));
                    assertEquals(rxMetadataRequest1.getCount(), metadataList.size());
                    for (Metadata metadata : list1) {
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
    }

    public void testRxLibraryTableOfContentLoadRequest() throws Exception {
        maxLevel = 5;
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = getDataManager();
        Library library = generateLibrary();
        dataManager.getRemoteContentProvider().addLibrary(library);
        final Map<String, Library[]> map = new HashMap<>();
        getNestedLibrary(dataManager.getRemoteContentProvider(), library.getIdString(), map, 0);

        RxLibraryTableOfContentLoadRequest request = new RxLibraryTableOfContentLoadRequest(dataManager, library);
        request.execute(new RxCallback<RxLibraryTableOfContentLoadRequest>() {
            @Override
            public void onNext(RxLibraryTableOfContentLoadRequest rxLibraryTableOfContentLoadRequest) {
                LibraryTableOfContentEntry entry = rxLibraryTableOfContentLoadRequest.getLibraryTableOfContentEntry();
                List<LibraryTableOfContentEntry> children = entry.children;
                assertFalse(CollectionUtils.isNullOrEmpty(children));
                assertNested(children, map);
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

    public void testRxModifyLibraryRequest() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final DataManager dataManager = getDataManager();
        final Library library = generateLibrary();
        String initName = TestUtils.randString();
        library.setName(initName);
        dataManager.getRemoteContentProvider().addLibrary(library);
        Library library1 = dataManager.getRemoteContentProvider().loadLibrary(library.getIdString());
        assertNotNull(library1);
        assertEquals(library.getName(), library1.getName());
        final String changedName = TestUtils.randString();
        library.setName(changedName);
        RxModifyLibraryRequest request = new RxModifyLibraryRequest(dataManager, library);
        request.execute(new RxCallback<RxModifyLibraryRequest>() {
            @Override
            public void onNext(RxModifyLibraryRequest rxModifyLibraryRequest) {
                Library library2 = dataManager.getRemoteContentProvider().loadLibrary(library.getIdString());
                assertLibrary(rxModifyLibraryRequest, library2);
                assertEquals(library2.getName(), changedName);
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

    private DataManager getDataManager() {
        init();
        DataManager dataManager = new DataManager();
        dataManager.getRemoteContentProvider().clearLibrary();
        dataManager.getRemoteContentProvider().clearThumbnails();
        dataManager.getRemoteContentProvider().clearMetadata();
        dataManager.getRemoteContentProvider().clearMetadataCollection();
        return dataManager;
    }

    private void assertNested(List<LibraryTableOfContentEntry> children, Map<String, Library[]> map) {
        for (LibraryTableOfContentEntry child : children) {
            if (CollectionUtils.isNullOrEmpty(child.children)) {
                break;
            }
            Library[] libraries = map.get(child.library.getIdString());
            assertEquals(libraries.length, child.children.size());
            for (int i = 0; i < libraries.length; i++) {
                Library library1 = libraries[i];
                boolean equal = false;
                for (LibraryTableOfContentEntry contentEntry : child.children) {
                    if (contentEntry.library.getIdString().equals(library1.getIdString())) {
                        equal = true;
                    }
                    assertNested(contentEntry.children, map);
                }
                assertTrue(equal);
            }
        }
    }

    private int getNestedLibrary(DataProviderBase dataProviderBase, String parentIdString, Map<String, Library[]> map, int currentFloor) {
        if (currentFloor >= maxLevel) {
            return 0;
        }
        currentFloor++;
        int total = 0;
        int randomCount = TestUtils.randInt(3, 5);
        total += randomCount;
        Log.e("###perFloorCount:", String.valueOf(randomCount));
        Library[] libraries = RxMetadataTest.getRandomLibrary(randomCount);
        map.put(parentIdString, libraries);
        for (Library library : libraries) {
            library.setParentUniqueId(parentIdString);
            dataProviderBase.addLibrary(library);
            total += getNestedLibrary(dataProviderBase, library.getIdString(), map, currentFloor);
        }
        return total;
    }

    private int getRandomInt(int max, int filter) {
        if (max == 0) {
            return 0;
        }
        int result = TestUtils.randInt(0, max);
        if (result != filter) {
            return result;
        }
        return getRandomInt(max, filter);
    }
}
