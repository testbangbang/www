package com.onyx.android.sdk;

import android.app.Application;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.facebook.common.references.CloseableReference;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.db.table.OnyxMetadataCollectionProvider;
import com.onyx.android.sdk.data.model.DataModel;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.MetadataCollection_Table;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.ReadingProgress;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.provider.RemoteDataProvider;
import com.onyx.android.sdk.data.request.data.db.LibraryClearRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryDeleteRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxLibraryLoadRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxMetadataRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxRecentDataRequest;
import com.onyx.android.sdk.data.rxrequest.data.db.RxRemoveFromLibraryRequest;
import com.onyx.android.sdk.data.utils.DataModelUtil;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.DateTimeUtil;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.utils.DateTimeUtil.DATE_FORMAT_YYYYMMDD_HHMMSS;
import static com.onyx.android.sdk.utils.TestUtils.defaultContentTypes;
import static com.onyx.android.sdk.utils.TestUtils.generateRandomFile;
import static com.onyx.android.sdk.utils.TestUtils.randString;
import static com.onyx.android.sdk.utils.TestUtils.randomStringList;

/**
 * Created by john on 29/10/2017.
 * adb shell pm grant com.onyx.android.sdk.dataprovider.test android.permission.WRITE_EXTERNAL_STORAGE
 * adb shell pm grant com.onyx.android.sdk.dataprovider.test android.permission.READ_EXTERNAL_STORAGE
 */

public class RxMetadataTest extends ApplicationTestCase<Application> {

    private static boolean dbInit = false;
    private static int maxLevel;

    public RxMetadataTest() {
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

    public static Metadata randomReadingMetadata(final String parent, boolean hasExtension) {
        Metadata metadata = null;
        try {
            File file = generateRandomFile(parent, hasExtension);
            metadata = new Metadata();
            String md5 = FileUtils.computeMD5(file);
            metadata.setIdString(md5);
            metadata.setName(file.getName());
            metadata.setLocation(file.getAbsolutePath());
            metadata.setNativeAbsolutePath(file.getAbsolutePath());
            metadata.setSize(file.length());
            metadata.setType(FileUtils.getFileExtension(file.getName()));
            int value = TestUtils.randInt(0, 10);
            if (value < 5) {
                metadata.setProgress(new ReadingProgress(TestUtils.randInt(50, 100), 100).toString());
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            return metadata;
        }
    }

    public static MetadataCollection randomMetadataCollection(String libraryUid, String md5) {
        MetadataCollection collection = new MetadataCollection();
        collection.setLibraryUniqueId(libraryUid);
        collection.setDocumentUniqueId(md5);
        collection.setIdString(generateRandomUUID());
        return collection;
    }

    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public DataProviderBase getProviderBase() {
        init();
        return DataProviderManager.getRemoteDataProvider();
    }

    public static Metadata randomMetadata(final String parent, boolean ext) {
        Metadata metadata = null;
        try {
            File file = generateRandomFile(parent, ext);
            metadata = new Metadata();
            metadata.setIdString(file.getAbsolutePath());
            metadata.setHashTag(FileUtils.computeMD5(file));
            metadata.setName(file.getName());
            metadata.setLocation(file.getAbsolutePath());
            metadata.setNativeAbsolutePath(file.getAbsolutePath());
            metadata.setSize(file.length());
            metadata.setType(FileUtils.getFileExtension(file.getName()));
            metadata.setAuthors(randString());
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            return metadata;
        }
    }

    public static String testFolder() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        return path;
    }

    public DataProviderBase getProviderBaseAndClearTable() {
        init();
        DataProviderBase dataProviderBase = getProviderBase();
        dataProviderBase.clearMetadata();
        dataProviderBase.clearLibrary();
        dataProviderBase.clearMetadataCollection();
        return dataProviderBase;
    }

    public void testMetadataSave() {
        init();
        DataProviderBase dataProvider = getProviderBaseAndClearTable();

        List<String> authors = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        final Metadata result = dataProvider.findMetadataByHashTag(getContext(), origin.getNativeAbsolutePath(), origin.getHashTag());
        assertNotNull(result);
        assertEquals(result.getIdString(), origin.getIdString());

        dataProvider.removeMetadata(getContext(), origin);
        final Metadata anotherResult = dataProvider.findMetadataByHashTag(getContext(), origin.getNativeAbsolutePath(), origin.getHashTag());
        assertFalse(anotherResult.hasValidId());
    }

    public void testMetadataSave2() {
        init();
        DataProviderBase dataProvider = getProviderBaseAndClearTable();

        List<String> authors = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);
    }

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void testRxMetadataRequest() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DataManager dataManager = new DataManager();

        QueryArgs args = QueryBuilder.libraryAllBookQuery(null, SortBy.CreationTime, SortOrder.Desc);
        RxMetadataRequest request = new RxMetadataRequest(dataManager, args, false);
        request.execute(new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest rxMetadataRequest) {
                assertFalse(rxMetadataRequest.getList().isEmpty());
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    public void testRxLibraryDeleteRequest() throws Exception {
        init();
        maxLevel = 2;
        Debug.setDebug(true);
        DataManager dataManager = new DataManager();
        dataManager.getRemoteContentProvider().clearLibrary();
        dataManager.getRemoteContentProvider().clearMetadataCollection();
        dataManager.getRemoteContentProvider().clearMetadata();
        Library topLibrary = getRandomLibrary();
        dataManager.getRemoteContentProvider().addLibrary(topLibrary);
        int libraryCount = getNestedLibrary(topLibrary.getIdString(), 0);
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, topLibrary.getIdString());
        assertTrue(CollectionUtils.getSize(libraryList) == libraryCount);
        libraryList.add(0, topLibrary);

        int total = 0;
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            int metadataCount = TestUtils.randInt(20, 22);
            total += metadataCount;
            for (int j = 0; j < metadataCount; j++) {
                Metadata meta = RxMetadataTest.randomMetadata(RxMetadataTest.testFolder(), true);
                dataManager.getRemoteContentProvider().saveMetadata(getContext(), meta);
                MetadataCollection collection = MetadataCollection.create(meta.getIdString(), library.getIdString());
                dataManager.getRemoteContentProvider().addMetadataCollection(getContext(), collection);
            }
        }

        int libraryIndex = 1;
        do {
            List<Library> tmpList = new ArrayList<>();
            libraryIndex = getRandomInt(libraryList.size() - 1, 0);
            final Library parentLibrary = libraryList.get(libraryIndex);
            DataManagerHelper.loadLibraryRecursive(dataManager, tmpList, parentLibrary.getIdString());
            tmpList.add(0, parentLibrary);

            int count = 0;
            for (Library library : tmpList) {
                count += dataManager.getRemoteContentProvider().loadMetadataCollection(getContext(), library.getIdString()).size();
            }
            QueryArgs args = QueryBuilder.libraryAllBookQuery(parentLibrary.getParentUniqueId(), SortBy.CreationTime, SortOrder.Desc);
            count += dataManager.getRemoteContentProvider().count(getContext(), args);

            //calculate time when deleting
            final int calCount = count;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Benchmark benchMark = new Benchmark();
            RxLibraryDeleteRequest request = new RxLibraryDeleteRequest(dataManager, parentLibrary);
            request.execute(new RxCallback<RxLibraryDeleteRequest>() {
                @Override
                public void onNext(RxLibraryDeleteRequest rxLibraryDeleteRequest) {
                    benchMark.reportError("##testDeleteLibraryRequest,metaCollectionCount:" + calCount);
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

            assertTrue(count == dataManager.getRemoteContentProvider().count(getContext(), QueryBuilder.libraryAllBookQuery(parentLibrary.getParentUniqueId(),
                    SortBy.CreationTime, SortOrder.Desc)));
            for (Library library : tmpList) {
                for (Library tmp : libraryList) {
                    if (tmp.getIdString().equalsIgnoreCase(library.getIdString())) {
                        libraryList.remove(tmp);
                        break;
                    }
                }
            }
        } while (libraryIndex != 0);
        //test all book when all the library was deleted.
        runTestLibraryAllBooksAndCreatedAtDesc(null, -1, total, total / 10);
        libraryList.clear();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, null);
        assertTrue(libraryList.size() == 0);
    }

    public void testClearLibraryRequest() throws Exception {
        maxLevel = 2;
        Debug.setDebug(true);
        clearTestFolder();

        DataProviderBase providerBase = getProviderBaseAndClearTable();

        DataManager dataManager = new DataManager();

        Library topLibrary = getRandomLibrary();
        providerBase.addLibrary(topLibrary);
        int libraryCount = getNestedLibrary(topLibrary.getIdString(), 0);
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, topLibrary.getIdString());
        assertTrue(CollectionUtils.getSize(libraryList) == libraryCount);
        libraryList.add(0, topLibrary);

        int total = 0;
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            int metadataCount = TestUtils.randInt(50, 55);
            total += metadataCount;
            for (int j = 0; j < metadataCount; j++) {
                Metadata meta = randomMetadata(testFolder(), true);
                providerBase.saveMetadata(getContext(), meta);
                MetadataCollection collection = MetadataCollection.create(meta.getIdString(), library.getIdString());
                providerBase.addMetadataCollection(getContext(), collection);
            }
        }

        int libraryIndex = 1;
        do {
            List<Library> tmpList = new ArrayList<>();
            libraryIndex = getRandomInt(libraryList.size() - 1, 0);
            Library parentLibrary = libraryList.get(libraryIndex);
            DataManagerHelper.loadLibraryRecursive(dataManager, tmpList, parentLibrary.getIdString());

            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Benchmark benchMark = new Benchmark();
            LibraryClearRequest clearRequest = new LibraryClearRequest(parentLibrary);
            dataManager.submit(getContext(), clearRequest, new BaseCallback() {
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
            if (!CollectionUtils.isNullOrEmpty(tmpList)) {
                benchMark.reportError("##testClearLibraryRequest,libraryCount:" + CollectionUtils.getSize(tmpList));
            }

            //test if empty
            QueryArgs args = QueryBuilder.libraryAllBookQuery(parentLibrary.getIdString(), SortBy.CreationTime, SortOrder.Desc);
            assertTrue(providerBase.count(getContext(), args) == 0);
            List<Library> checkEmptyLibraryList = new ArrayList<>();
            DataManagerHelper.loadLibraryRecursive(dataManager, checkEmptyLibraryList, parentLibrary.getIdString());
            assertTrue(CollectionUtils.getSize(checkEmptyLibraryList) == 0);

            if (tmpList.size() == 0) {
                tmpList.add(parentLibrary);
            }
            for (Library library : tmpList) {
                for (Library tmp : libraryList) {
                    if (tmp.getIdString().equalsIgnoreCase(library.getIdString())) {
                        libraryList.remove(tmp);
                        break;
                    }
                }
            }
        } while (libraryIndex != 0);
        //test all book when all the library does't contains metadata
        runTestLibraryAllBooksAndCreatedAtDesc(null, -1, total, total / 10);
        libraryList.clear();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, null);
        assertTrue(CollectionUtils.getSize(libraryList) == 1);// only topLibrary
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

    public void testLibraryRequest() throws Exception {
        init();
        maxLevel = 2;
        Debug.setDebug(true);
        clearTestFolder();
        DataManager dataManager = new DataManager();
        DataProviderBase remoteContentProvider = dataManager.getRemoteContentProvider();
        remoteContentProvider.clearLibrary();
        remoteContentProvider.clearMetadata();
        remoteContentProvider.clearMetadataCollection();
        remoteContentProvider.clearThumbnails();

        Library topLibrary = MetadataTest.getRandomLibrary();
        remoteContentProvider.addLibrary(topLibrary);
        int libraryCount = getNestedLibrary(topLibrary.getIdString(), 0);
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, topLibrary.getIdString());
        assertTrue(CollectionUtils.getSize(libraryList) == libraryCount);
        libraryList.add(0, topLibrary);

        final Map<String, Integer> libraryMetaCountMap = new HashMap<>();
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            int metadataCount = TestUtils.randInt(100, 200);
            libraryMetaCountMap.put(library.getIdString(), metadataCount);
            for (int j = 0; j < metadataCount; j++) {
                Metadata meta = randomMetadata(testFolder(), true);
                remoteContentProvider.saveMetadata(getContext(), meta);
                MetadataCollection collection = MetadataCollection.create(meta.getIdString(), library.getIdString());
                remoteContentProvider.addMetadataCollection(getContext(), collection);
            }
        }

        for (int i = 0; i < libraryList.size(); i++) {
            final Library library = libraryList.get(i);
            final int index = i;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Benchmark benchMark = new Benchmark();
            final QueryArgs args = QueryBuilder.libraryAllBookQuery(library.getIdString(), SortBy.CreationTime, SortOrder.Desc);
            final RxLibraryLoadRequest request = new RxLibraryLoadRequest(dataManager, args);
            RxLibraryLoadRequest.setAppContext(getContext());
            request.execute(new RxCallback<RxLibraryLoadRequest>() {
                @Override
                public void onNext(RxLibraryLoadRequest rxLibraryLoadRequest) {
                    benchMark.reportError("##testLibraryRequest,index:" + index);
                    List<Library> childLibraryList = rxLibraryLoadRequest.getLibraryList();
                    if (!CollectionUtils.isNullOrEmpty(childLibraryList)) {
                        for (Library tmp : childLibraryList) {
                            assertEquals(tmp.getParentUniqueId(), library.getIdString());
                        }
                    }
                    assertTrue(rxLibraryLoadRequest.getTotalCount() == libraryMetaCountMap.get(library.getIdString()));
                    Metadata tmp = rxLibraryLoadRequest.getBookList().get(0);
                    for (Metadata metadata : rxLibraryLoadRequest.getBookList()) {
                        assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
                        tmp = metadata;
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
            awaitCountDownLatch(countDownLatch);
        }
    }

    public void testRxRemoveFromLibraryRequest() throws Exception {
        maxLevel = 2;
        Debug.setDebug(true);
        clearTestFolder();
        init();
        DataProviderBase providerBase = getProviderBaseAndClearTable();

        DataManager dataManager = new DataManager();
        dataManager.getRemoteContentProvider().clearLibrary();
        dataManager.getRemoteContentProvider().clearMetadataCollection();
        dataManager.getRemoteContentProvider().clearMetadata();

        Library topLibrary = getRandomLibrary();
        providerBase.addLibrary(topLibrary);
        int libraryCount = getNestedLibrary(topLibrary.getIdString(), 0);
        List<Library> libraryList = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, libraryList, topLibrary.getIdString());
        assertTrue(CollectionUtils.getSize(libraryList) == libraryCount);
        libraryList.add(0, topLibrary);

        Map<String, Integer> libraryMetaCountMap = new HashMap<>();
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            int metadataCount = TestUtils.randInt(10, 15);
            libraryMetaCountMap.put(library.getIdString(), metadataCount);
            for (int j = 0; j < metadataCount; j++) {
                Metadata meta = randomMetadata(testFolder(), true);
                providerBase.saveMetadata(getContext(), meta);
                MetadataCollection collection = MetadataCollection.create(meta.getIdString(), library.getIdString());
                providerBase.addMetadataCollection(getContext(), collection);
            }
        }

        int topTotalCount = 0;
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            final int limit = TestUtils.randInt(14, 15);
            topTotalCount += limit;
            QueryArgs args = QueryBuilder.libraryAllBookQuery(library.getIdString(), SortBy.CreationTime, SortOrder.Desc);
            args.limit = limit;
            List<Metadata> list = dataManager.getRemoteContentProvider().findMetadataByQueryArgs(getContext(), args);
            final int index = i;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Benchmark benchMark = new Benchmark();
            RxRemoveFromLibraryRequest removeRequest = new RxRemoveFromLibraryRequest(dataManager, library, list);
            removeRequest.execute(new RxCallback<RxRemoveFromLibraryRequest>() {
                @Override
                public void onNext(RxRemoveFromLibraryRequest removeFromLibraryRequest) {
                    benchMark.reportError("##testRemoveFromLibraryRequest,index:" + index + "limit:" + limit);
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

            args.limit = Integer.MAX_VALUE;
            assertTrue(dataManager.getRemoteContentProvider().count(getContext(), args) == libraryMetaCountMap.get(library.getIdString()) - limit);

            //test top library count
            args = QueryBuilder.libraryAllBookQuery(null, SortBy.CreationTime, SortOrder.Desc);
            assertTrue(dataManager.getRemoteContentProvider().count(getContext(), args) == topTotalCount);
        }
    }

    public void testBookListWithoutLibraryRequest() throws Exception {
        clearTestFolder();
        Debug.setDebug(true);

        final DataProviderBase providerBase = getProviderBaseAndClearTable();
        providerBase.clearMetadata();

        long total = 0;
        int totalReadingBookCount = 0;
        int totalFinishedBookCount = 0;

        for (int r = 0; r < 10; ++r) {
            final int limit = TestUtils.randInt(15, 15);
            for (int i = 0; i < limit; i++) {
                Metadata meta = randomMetadata(testFolder(), true);
                int side = TestUtils.randInt(10, 10);
                if (side >= 10) {
                    ++totalFinishedBookCount;
                    meta.setLastAccess(new Date(System.currentTimeMillis()));
                    meta.setReadingStatus(Metadata.ReadingStatus.FINISHED);
                    meta.setProgress("100/100");
                } else if (side >= 10) {
                    ++totalReadingBookCount;
                    meta.setLastAccess(new Date(System.currentTimeMillis()));
                    meta.setReadingStatus(Metadata.ReadingStatus.READING);
                    meta.setProgress("" + side + "/100");
                }
                providerBase.saveMetadata(getContext(), meta);
            }
            total += limit;
            final long value = total;
            Log.e("####BookListPagination", "record generated: " + limit);
            runTestAllBooksAndDescCreatedAt(value, limit);
            runTestNewBooksAndAscSize(total - totalReadingBookCount - totalFinishedBookCount, limit);
            runTestReadingBooksAndDescName(totalReadingBookCount, limit);
            runTestReadedBooksAndDescAuthor(totalFinishedBookCount, limit);
            Log.e("####BookListPagination", "round: " + r + " finished. ");
        }
    }

    public void testBookListWithLibraryRequest() throws Exception {
        clearTestFolder();
        Debug.setDebug(true);

        final DataProviderBase providerBase = getProviderBaseAndClearTable();

        // generate library
        int libraryCount = TestUtils.randInt(10, 20);
        final HashMap<String, Library> libraryMap = new HashMap<>();
        final List<Library> libraryList = new ArrayList<>();
        for (int i = 0; i < libraryCount; ++i) {
            final Library library = getRandomLibrary();
            providerBase.addLibrary(library);
            libraryMap.put(library.getIdString(), library);
            libraryList.add(library);
        }

        // setup metadata collection with library and metadata.
        final HashMap<String, HashSet<String>> metaCollectionMap = new HashMap<>();
        int metadataCount = TestUtils.randInt(100, 200);
        for (int i = 0; i < metadataCount; ++i) {
            final Metadata metadata = randomMetadata(testFolder(), true);
            providerBase.saveMetadata(getContext(), metadata);
            int index = TestUtils.randInt(0, libraryCount - 1);
            final Library library = libraryList.get(index);
            final MetadataCollection metadataCollection = MetadataCollection.create(metadata.getIdString(), library.getIdString());
            providerBase.addMetadataCollection(getContext(), metadataCollection);
            HashSet<String> list = metaCollectionMap.get(library.getIdString());
            if (list == null) {
                list = new HashSet<>();
                metaCollectionMap.put(library.getIdString(), list);
            }
            list.add(metadata.getIdString());
        }

        DataManager dataManager = new DataManager();
        for (int i = 0; i < libraryCount; ++i) {
            final String libId = libraryList.get(i).getIdString();
            final HashSet<String> mc = metaCollectionMap.get(libId);
            if (mc == null) {
                continue;
            }
            final QueryArgs queryArgs = QueryBuilder.libraryAllBookQuery(libId, SortBy.CreationTime, SortOrder.Desc);
            final HashSet<String> set = new HashSet<>();
            set.addAll(mc);
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final RxMetadataRequest metadataRequest = new RxMetadataRequest(dataManager, queryArgs);
            metadataRequest.execute(new RxCallback<RxMetadataRequest>() {
                @Override
                public void onNext(RxMetadataRequest request) {
                    assertNotNull(metadataRequest.getList());
                    assertTrue(metadataRequest.getCount() == set.size());
                    assertTrue(metadataRequest.getList().size() == set.size());
                    for (Metadata metadata : metadataRequest.getList()) {
                        assertTrue(set.contains(metadata.getIdString()));
                        set.remove(metadata.getIdString());
                    }
                    assertTrue(set.isEmpty());
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

    public void testLibraryIntegratedFunc() throws Exception {
        maxLevel = 2;
        clearTestFolder();
        Debug.setDebug(true);
        init();
        DataManager dataManager = new DataManager();
        DataProviderBase providerBase = dataManager.getRemoteContentProvider();
        providerBase.clearMetadata();
        providerBase.clearMetadataCollection();
        providerBase.clearLibrary();

        //get nestedIn Library
        Library topLibrary = getRandomLibrary();
        providerBase.addLibrary(topLibrary);
        final int libraryCount = getNestedLibrary(topLibrary.getIdString(), 0);
        Log.e("###totalLibraryCount", String.valueOf(libraryCount));
        List<Library> list = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(new DataManager(), list, topLibrary.getIdString());
        assertTrue(libraryCount == CollectionUtils.getSize(list));
        final Map<String, Integer> libraryBookCountMap = new HashMap<>();
        final Map<String, Integer> libraryAddBookFreqMap = new HashMap<>();
        String[] libraryIdSet = new String[libraryCount];
        for (int i = 0; i < libraryCount; i++) {
            libraryIdSet[i] = list.get(i).getIdString();
            libraryBookCountMap.put(libraryIdSet[i], 0);
            libraryAddBookFreqMap.put(libraryIdSet[i], 0);
        }

        long total = 0;
        for (int r = 0; r < 35; ++r) {
            final int limit = TestUtils.randInt(150, 155);
            final int addToLibraryCount = TestUtils.randInt(limit - 8, limit - 2);
            final int finishBookCount = 35;
            final int readingBookCount = 30;
            final int tagsBooKCount = 70;
            final int searchCount = 74;
            final String search = "1234567890-=";
            int libraryIndex = TestUtils.randInt(0, libraryIdSet.length - 1);
            String libraryIdString = libraryIdSet[libraryIndex];
            int originCount = libraryBookCountMap.get(libraryIdString);
            libraryBookCountMap.put(libraryIdString, originCount + addToLibraryCount);
            int originFreq = libraryAddBookFreqMap.get(libraryIdString);
            libraryAddBookFreqMap.put(libraryIdString, originFreq + 1);
            for (int i = 0; i < limit; i++) {
                Metadata meta = randomMetadata(testFolder(), true);
                if (i < finishBookCount) {
                    meta.setLastAccess(new Date(System.currentTimeMillis()));
                    meta.setReadingStatus(Metadata.ReadingStatus.FINISHED);
                    meta.setProgress("100/100");
                } else if (i < finishBookCount + readingBookCount) {
                    meta.setLastAccess(new Date(System.currentTimeMillis()));
                    meta.setReadingStatus(Metadata.ReadingStatus.READING);
                    meta.setProgress("40/100");
                }
                if (i < tagsBooKCount) {
                    Set<String> tagSet = getFormatTagSet();
                    String tagsString = StringUtils.join(tagSet, Metadata.DELIMITER);
                    int removeIndex = tagsString.indexOf(getRandomFormatTag());
                    if (removeIndex != 0) {
                        tagsString = tagsString.substring(0, removeIndex);
                    }
                    meta.setTags(tagsString);
                }
                if (i < searchCount) {
                    String title = generateRandomUUID();
                    int replaceIndex = TestUtils.randInt(0, title.length() - 4);
                    int endIndex = TestUtils.randInt(replaceIndex, title.length() - 1);
                    meta.setTitle(title.replaceAll(title.substring(replaceIndex, endIndex), search));
                    Log.e("", "testLibraryIntegratedFunc: save:" + meta.getTitle());
                }
                providerBase.saveMetadata(getContext(), meta);

                if (i < addToLibraryCount) {
                    MetadataCollection metadataCollection = new MetadataCollection();
                    metadataCollection.setLibraryUniqueId(libraryIdString);
                    metadataCollection.setDocumentUniqueId(meta.getNativeAbsolutePath());
                    providerBase.addMetadataCollection(getContext(), metadataCollection);
                }
            }

            total += limit;
            final long totalCount = total;
            int libraryBookTotalCount = 0;
            for (String s : libraryIdSet) {
                libraryBookTotalCount += libraryBookCountMap.get(s);
            }
            Log.e("##LibraryIntegratedFunc", "round generated: " + limit);
            Log.e("##LibraryIntegratedFunc", "total round generated: " + totalCount);
            Log.e("##LibraryIntegratedFunc", "addToLibraryBookTotalCount: " + libraryBookTotalCount);
            runTestLibraryAllBooksAndCreatedAtDesc(null, -1, totalCount - libraryBookTotalCount, limit);
            runTestLibraryAllBooksAndCreatedAtDesc(libraryIdSet, libraryIndex, libraryBookCountMap.get(libraryIdString), limit);
            runTestLibraryNewBooksAndAscSize(libraryIdSet, libraryIndex, libraryBookCountMap.get(libraryIdString) - libraryAddBookFreqMap.get(libraryIdString) * (finishBookCount + readingBookCount), limit);
            runTestLibraryReadingBooksAndDescName(libraryIdSet, libraryIndex, libraryAddBookFreqMap.get(libraryIdString) * readingBookCount, limit);//4,6,31
            runTestLibraryReadedBooksAndDescRecentlyRead(libraryIdSet, libraryIndex, libraryAddBookFreqMap.get(libraryIdString) * finishBookCount, limit);
            runTestLibraryTagsBooksAndDescFileType(libraryIdSet, libraryIndex, getFormatTagSet(), libraryAddBookFreqMap.get(libraryIdString) * tagsBooKCount, limit);
            runTestLibrarySearchBooksAndDescTitle(libraryIdSet, libraryIndex, search, libraryAddBookFreqMap.get(libraryIdString) * searchCount, limit);
            Log.e("##LibraryIntegratedFunc", "round: " + r + " finished. ");
        }

        //test path list query
        final QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.Name, SortOrder.Desc);
        queryArgs.propertyList.add(Metadata_Table.nativeAbsolutePath);
        runTestMetadataQueryArgs("##OnlyPathListQuery", total, 100, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                assertNotNull(metadataRequest.getList().get(0).getNativeAbsolutePath());
                assertNull(metadataRequest.getList().get(0).getName());
            }
        });

        //test delete topLibrary
        Benchmark benchMark = new Benchmark();
        for (Library library : list) {
            Benchmark perMark = new Benchmark();
            if (providerBase instanceof RemoteDataProvider) {
                ContentValues values = new ContentValues();
                values.put(MetadataCollection_Table.libraryUniqueId.getNameAlias().name(), (String) null);
                FlowManager.getContext().getContentResolver().update(OnyxMetadataCollectionProvider.CONTENT_URI,
                        values,
                        ConditionGroup.clause().and(MetadataCollection_Table.libraryUniqueId.is(library.getIdString())).getQuery(),
                        null);
            } else if (providerBase instanceof LocalDataProvider) {
                SQLite.update(MetadataCollection.class)
                        .set(MetadataCollection_Table.libraryUniqueId.eq(topLibrary.getParentUniqueId()))
                        .where(MetadataCollection_Table.libraryUniqueId.is(library.getIdString()))
                        .execute();
            }
            perMark.report("####deletePerLibrary");
        }
        benchMark.report("####deleteTopLibrary,totalCount:" + total);
        runTestLibraryAllBooksAndCreatedAtDesc(null, -1, total, (int) (total / 11));
    }

    private void runTestLibrarySearchBooksAndDescTitle(String[] libraryIdSet, int libraryIndex, String search,
                                                       long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.librarySearchQuery(libraryIdSet[libraryIndex],
                search, SortBy.BookTitle, SortOrder.Desc);
        runTestMetadataQueryArgs("####SearchBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNotNull(metadata.getTitle());
                    assertTrue(tmp.getTitle().compareTo(metadata.getTitle()) >= 0);
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestLibraryTagsBooksAndDescFileType(String[] libraryIdSet, int libraryIndex, Set<String> tags,
                                                        long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.libraryTagsFilterQuery(libraryIdSet[libraryIndex],
                tags, SortBy.FileType, SortOrder.Desc);
        runTestMetadataQueryArgs("####TagsBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNotNull(metadata.getType());
                    assertTrue(tmp.getType().compareTo(metadata.getType()) >= 0);
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestLibraryReadedBooksAndDescRecentlyRead(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.libraryFinishReadQuery(libraryIdSet[libraryIndex],
                SortBy.RecentlyRead, SortOrder.Desc);
        runTestMetadataQueryArgs("####ReadedBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNotNull(metadata.getProgress());
                    assertNotNull(metadata.getLastAccess());
                    assertTrue(tmp.getLastAccess().getTime() >= metadata.getLastAccess().getTime());
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestLibraryReadingBooksAndDescName(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.libraryRecentReadingQuery(libraryIdSet[libraryIndex],
                SortBy.Name, SortOrder.Desc);
        runTestMetadataQueryArgs("####ReadingBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNotNull(metadata.getProgress());
                    assertNotNull(metadata.getLastAccess());
                    assertTrue(tmp.getName().compareTo(metadata.getName()) >= 0);
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestLibraryNewBooksAndAscSize(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.libraryBookListNewQuery(libraryIdSet[libraryIndex],
                SortBy.Size, SortOrder.Asc);
        runTestMetadataQueryArgs("####NewBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNull(metadata.getProgress());
                    assertNull(metadata.getLastAccess());
                    assertTrue(tmp.getSize() <= metadata.getSize());
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestLibraryAllBooksAndCreatedAtDesc(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) throws Exception {
        String libraryIdString = null;
        if (libraryIdSet != null && libraryIdSet.length > 0 && libraryIndex >= 0) {
            libraryIdString = libraryIdSet[libraryIndex];
        }
        final QueryArgs queryArgs = QueryBuilder.libraryAllBookQuery(libraryIdString,
                SortBy.CreationTime, SortOrder.Desc);
        runTestMetadataQueryArgs("####AllBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
                    tmp = metadata;
                }
            }
        });
    }

    public static String[] getFormatTags() {
        String tags[] = new String[]{"pdf", "epub", "djvu", "mobi", "cb2", "zip", "rar", "apk", "iso"};
        return tags;
    }

    public static Set<String> getFormatTagSet() {
        String tags[] = getFormatTags();
        Set<String> tagSet = new HashSet<>();
        for (String tag : tags) {
            tagSet.add(tag);
        }
        return tagSet;
    }

    public static String getRandomFormatTag() {
        Random random = new Random();
        return getFormatTags()[random.nextInt(getFormatTags().length)];
    }

    private void loadRecursiveLibraryList(DataProviderBase providerBase, List<Library> resultList, String parentIdString) {
        List<Library> libraryList = providerBase.loadAllLibrary(parentIdString, null);
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        resultList.addAll(libraryList);
        for (Library library : libraryList) {
            loadRecursiveLibraryList(providerBase, resultList, library.getIdString());
        }
    }

    private void runTestReadedBooksAndDescAuthor(final long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.finishReadQuery(SortBy.Author, SortOrder.Desc);
        runTestMetadataQueryArgs("####runTestReadedBooksAndDescAuthor", totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNotNull(metadata.getProgress());
                    assertNotNull(metadata.getLastAccess());
                    assertTrue(metadata.isFinished());
                    assertTrue(tmp.getAuthors().compareTo(metadata.getAuthors()) >= 0);
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestReadingBooksAndDescName(final long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.recentReadingQuery(SortBy.Name, SortOrder.Desc);
        runTestMetadataQueryArgs("####runTestReadingBooksAndDescName", totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNotNull(metadata.getProgress());
                    assertNotNull(metadata.getLastAccess());
                    assertTrue(tmp.getName().compareTo(metadata.getName()) <= 0);
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestAllBooksAndDescCreatedAt(final long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.allBooksQuery(defaultContentTypes(),
                OrderBy.fromProperty(Metadata_Table.createdAt).descending());
        runTestMetadataQueryArgs("####runTestAllBooksAndDescCreatedAt", totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestNewBooksAndAscSize(final long totalCount, int perCount) throws Exception {
        final QueryArgs queryArgs = QueryBuilder.newBookListQuery(SortBy.Size, SortOrder.Asc);
        runTestMetadataQueryArgs("####runTestAllBooksAndDescCreatedAt", totalCount, perCount, queryArgs, new RxCallback<RxMetadataRequest>() {
            @Override
            public void onNext(RxMetadataRequest metadataRequest) {
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNull(metadata.getLastAccess());
                    assertTrue(tmp.getSize() <= metadata.getSize());
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestMetadataQueryArgs(final String benchTag, final long totalCount, int perCount, final QueryArgs queryArgs, final RxCallback<RxMetadataRequest> callBack) throws Exception {
        final Benchmark benchMark = new Benchmark();
        DataManager dataManager = new DataManager();
        int block = 10;
        final int limit = TestUtils.randInt(20, 20);
        queryArgs.limit = limit;
        for (int i = 0; i < block; ++i) {
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            benchMark.restart();
            queryArgs.offset = (int) (i * totalCount / block);
            final int offset = queryArgs.offset;
            final RxMetadataRequest metadataRequest = new RxMetadataRequest(dataManager, queryArgs);
            metadataRequest.execute(new RxCallback<RxMetadataRequest>() {
                @Override
                public void onNext(RxMetadataRequest request) {
                    assertNotNull(metadataRequest.getList());
                    assertTrue(metadataRequest.getList().size() <= queryArgs.limit);
                    if (metadataRequest.getCount() != totalCount) {
                        Log.e(benchTag, "count not matched: " + metadataRequest.getCount() + "  " + totalCount);
                    }
                    assertTrue(metadataRequest.getCount() == totalCount);
                    if (!CollectionUtils.isNullOrEmpty(metadataRequest.getList())) {
                        callBack.onNext(request);
                    }
                    benchMark.report(benchTag + " count:" + totalCount + ",offset:" + offset + ",limit:" + limit);
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

    public static Library getRandomLibrary() {
        Library library = new Library();
        library.setIdString(generateRandomUUID());
        library.setName(generateRandomUUID());
        library.setDescription(generateRandomUUID());
        return library;
    }

    public static Library[] getRandomLibrary(int count) {
        Library[] list = new Library[count];
        for (int i = 0; i < count; i++) {
            list[i] = getRandomLibrary();
        }
        return list;
    }

    private int getNestedLibrary(String parentIdString, int currentFloor) {
        if (currentFloor >= maxLevel) {
            return 0;
        }
        currentFloor++;
        int total = 0;
        int randomCount = TestUtils.randInt(3, 5);
        total += randomCount;
        Log.e("###perFloorCount:", String.valueOf(randomCount));
        Library[] libraries = getRandomLibrary(randomCount);
        for (Library library : libraries) {
            library.setParentUniqueId(parentIdString);
            getProviderBase().addLibrary(library);
            total += getNestedLibrary(library.getIdString(), currentFloor);
        }
        return total;
    }

    private void clearTestFolder() {
        FileUtils.purgeDirectory(new File(testFolder()));
        FileUtils.mkdirs(testFolder());
    }

    public void testRecent() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        clearTestFolder();
        DataProviderBase dataProviderBase = getProviderBaseAndClearTable();
        final List<Metadata> recentlyAddMetadata = new ArrayList<>();
        for (int i = 0; i < getRandomInt(20, 100); i++) {
            Metadata metadata = randomMetadata(testFolder(), true);
            dataProviderBase.saveMetadata(getContext(), metadata);
            recentlyAddMetadata.add(metadata);
        }
        final List<Metadata> recentlyReadMetadata = new ArrayList<>();
        for (int i = 0; i < getRandomInt(20, 100); i++) {
            Metadata metadata = randomMetadata(testFolder(), true);
            metadata.setReadingStatus(1);
            dataProviderBase.saveMetadata(getContext(), metadata);
            recentlyReadMetadata.add(metadata);
        }

        RxRecentDataRequest recentAddRequest = new RxRecentDataRequest(new DataManager(), EventBus.getDefault());
        RxRecentDataRequest.setAppContext(getContext());
        recentAddRequest.execute(new RxCallback<RxRecentDataRequest>() {
            @Override
            public void onNext(RxRecentDataRequest dataRequest) {
                List<DataModel> targetAddList = new ArrayList<>();
                List<DataModel> targetReadList = new ArrayList<>();
                assertRecentAdd(dataRequest.getRecentlyAddMetadata());
                assertRecentlyRead(dataRequest.getRecentlyReadMetadata());
                Map<String, CloseableReference<Bitmap>> thumbnailMap = DataManagerHelper.loadThumbnailBitmapsWithCache(getContext(), dataRequest.getDataManager(), dataRequest.getRecentlyReadMetadata());
                DataModelUtil.metadataToDataModel(EventBus.getDefault(), targetAddList, recentlyAddMetadata, thumbnailMap, R.drawable.book_default_cover);
                RxLibraryTest.assertListEqual(dataRequest.getRecentAddList(), targetAddList);
                DataModelUtil.metadataToDataModel(EventBus.getDefault(), targetReadList, recentlyReadMetadata, thumbnailMap, R.drawable.library_default_cover);
                RxLibraryTest.assertListEqual(dataRequest.getRecentlyReadList(), targetReadList);
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

    private void assertRecentlyRead(List<Metadata> metadataList) {
        assertFalse(CollectionUtils.isNullOrEmpty(metadataList));
        Metadata tmp = metadataList.get(0);
        for (Metadata metadata : metadataList) {
            long tmpTime = DateTimeUtil.parse(DateTimeUtil.formatDate(tmp.getUpdatedAt()), DATE_FORMAT_YYYYMMDD_HHMMSS);
            long parse = DateTimeUtil.parse(DateTimeUtil.formatDate(metadata.getUpdatedAt()), DATE_FORMAT_YYYYMMDD_HHMMSS);
            assertTrue(tmpTime >= parse);
            tmp = metadata;
        }
    }

    private void assertRecentAdd(List<Metadata> metadataList) {
        assertFalse(CollectionUtils.isNullOrEmpty(metadataList));
        Metadata tmp = metadataList.get(0);
        for (Metadata metadata : metadataList) {
            long tmpTime = DateTimeUtil.parse(DateTimeUtil.formatDate(tmp.getCreatedAt()), DATE_FORMAT_YYYYMMDD_HHMMSS);
            long parse = DateTimeUtil.parse(DateTimeUtil.formatDate(metadata.getCreatedAt()), DATE_FORMAT_YYYYMMDD_HHMMSS);
            assertTrue(tmpTime >= parse);
            tmp = metadata;
        }
    }
}
