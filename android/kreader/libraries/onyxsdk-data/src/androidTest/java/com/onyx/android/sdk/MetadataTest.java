package com.onyx.android.sdk;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManagerHelper;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.db.table.OnyxMetadataCollectionProvider;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Library_Table;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.MetadataCollection_Table;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.model.ReadingProgress;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.provider.RemoteDataProvider;
import com.onyx.android.sdk.data.request.data.db.LibraryBuildRequest;
import com.onyx.android.sdk.data.request.data.db.LibraryClearRequest;
import com.onyx.android.sdk.data.request.data.db.LibraryDeleteRequest;
import com.onyx.android.sdk.data.request.data.db.LibraryLoadRequest;
import com.onyx.android.sdk.data.request.data.db.MetadataRequest;
import com.onyx.android.sdk.data.request.data.db.MoveToLibraryRequest;
import com.onyx.android.sdk.data.request.data.db.RemoveFromLibraryRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.utils.TestUtils.defaultContentTypes;
import static com.onyx.android.sdk.utils.TestUtils.generateRandomFile;
import static com.onyx.android.sdk.utils.TestUtils.randomStringList;

/**
 * Created by zhuzeng on 8/26/16.
 */
public class MetadataTest extends ApplicationTestCase<Application> {

    private static boolean dbInit = false;

    public MetadataTest() {
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
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            return metadata;
        }
    }

    public static String testFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
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

    public void testMetadataSaveWithJson() {
        init();
        DataProviderBase dataProvider = getProviderBaseAndClearTable();

        final String json = UUID.randomUUID().toString();
        Metadata origin = randomMetadata(testFolder(), true);
        dataProvider.saveDocumentOptions(getContext(), origin.getNativeAbsolutePath(), origin.getHashTag(), json);

        final Metadata result = dataProvider.findMetadataByHashTag(getContext(), origin.getNativeAbsolutePath(), origin.getHashTag());
        assertNotNull(result);
        assertEquals(result.getIdString(), origin.getIdString());
        assertEquals(result.getExtraAttributes(), json);
    }

    public void testName() throws Exception {

    }

    public void testQueryCriteriaAuthor() {
        init();

        DataProviderBase dataProvider = getProviderBaseAndClearTable();

        List<String> authors = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = randomMetadata(testFolder(), true);
            dataProvider.saveMetadata(getContext(), metadata);
        }

        QueryArgs args = QueryBuilder.allBooksQuery(defaultContentTypes(), SortBy.Name, SortOrder.Desc);
        List<Metadata> testList = dataProvider.findMetadataByQueryArgs(getContext(), args);
        assertNotNull(testList);
        assertTrue(testList.size() > 0);

        final QueryArgs queryCriteria = new QueryArgs();
        queryCriteria.author.addAll(authors);
        QueryBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadataByQueryArgs(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadataByQueryArgs(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == limit + 1);
    }

    public void testQueryCriteriaTitle() {
        init();

        DataProviderBase dataProvider = getProviderBaseAndClearTable();

        List<String> title = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setTitle(StringUtils.join(title, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = randomMetadata(testFolder(), true);
            dataProvider.saveMetadata(getContext(), metadata);
        }

        final QueryArgs queryCriteria = new QueryArgs();
        queryCriteria.title.addAll(title);
        QueryBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadataByQueryArgs(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadataByQueryArgs(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == limit + 1);
    }

    public void testQueryCriteriaSeries() {
        init();

        DataProviderBase dataProvider = getProviderBaseAndClearTable();

        List<String> series = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setSeries(StringUtils.join(series, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = randomMetadata(testFolder(), true);
            dataProvider.saveMetadata(getContext(), metadata);
        }

        QueryArgs queryCriteria = new QueryArgs();
        queryCriteria.series.addAll(series);
        QueryBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadataByQueryArgs(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadataByQueryArgs(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == limit + 1);
    }

    public void testQueryCriteriaTags() {
        init();

        DataProviderBase dataProvider = getProviderBaseAndClearTable();

        List<String> tags = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setTags(StringUtils.join(tags, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = randomMetadata(testFolder(), true);
            dataProvider.saveMetadata(getContext(), metadata);
        }

        final QueryArgs queryCriteria = new QueryArgs();
        queryCriteria.tags.addAll(tags);
        QueryBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadataByQueryArgs(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadataByQueryArgs(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == limit + 1);
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

    public static void assertRandomMetadata(Context context, DataProviderBase dataProvider, Metadata origin) {
        final Metadata result = dataProvider.findMetadataByHashTag(context, origin.getNativeAbsolutePath(), origin.getHashTag());
        assertNotNull(result);
        assertEquals(result.getIdString(), origin.getIdString());
    }

    public static Metadata getRandomMetadata() {
        List<String> authors = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        return origin;
    }

    public static Metadata[] getRandomMetadata(int count) {
        Metadata[] testMetadata = new Metadata[count];
        for (int i = 0; i < count; i++) {
            testMetadata[i] = getRandomMetadata();
        }
        return testMetadata;
    }

    public void testTagsFilterRequest() {
        init();

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final int count = 12;//at least 4
        final int divider = 3; // to be smaller than count
        final DataProviderBase providerBase = getProviderBaseAndClearTable();
        final String indexTag = getRandomFormatTag();
        providerBase.clearMetadata();
        final Metadata[] testMetadata = getRandomMetadata(count);
        for (int i = 0; i < count; i++) {
            testMetadata[i].setTags(getRandomFormatTag());
            if (i % divider == 0) {
                testMetadata[i].setTags(indexTag);
            }
            providerBase.saveMetadata(getContext(), testMetadata[i]);
            assertRandomMetadata(getContext(), providerBase, testMetadata[i]);
        }

        Set<String> tagSet = new HashSet<>();
        tagSet.add(indexTag);

        QueryArgs queryArgs = QueryBuilder.tagsFilterQuery(tagSet, SortBy.Name, SortOrder.Desc);
        DataManager dataManager = new DataManager();
        final MetadataRequest metadataRequest = new MetadataRequest(queryArgs);
        dataManager.submit(getContext(), metadataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(metadataRequest.getList());
                assertTrue(metadataRequest.getList().size() >= count / divider);

                boolean result = false;
                for (Metadata metadata : metadataRequest.getList()) {
                    for (Metadata test : testMetadata) {
                        result |= metadata.getIdString().equals(test.getIdString());
                    }
                    assertTrue(result);
                    assertTrue(metadata.getTags().contains(indexTag));
                    result = false;
                }

                providerBase.clearMetadata();
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    public void testBookListRequest() {
        init();

        final int count = 4;//at least 2
        final DataProviderBase providerBase = getProviderBaseAndClearTable();
        final Metadata[] testMetadata = new Metadata[count];
        for (int i = 0; i < count; i++) {
            testMetadata[i] = getRandomMetadata();
            providerBase.saveMetadata(getContext(), testMetadata[i]);
            assertRandomMetadata(getContext(), providerBase, testMetadata[i]);
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        QueryArgs queryArgs = QueryBuilder.allBooksQuery(defaultContentTypes(),
                OrderBy.fromProperty(Metadata_Table.createdAt).descending());
        DataManager dataManager = new DataManager();
        final MetadataRequest metadataRequest = new MetadataRequest(queryArgs);
        dataManager.submit(getContext(), metadataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(metadataRequest.getList());
                assertTrue(metadataRequest.getList().size() > 0);
                assertTrue(metadataRequest.getList().size() == count);
                boolean result = false;
                for (Metadata metadata : metadataRequest.getList()) {
                    for (Metadata test : testMetadata) {
                        result |= metadata.getIdString().equals(test.getIdString());
                    }
                    assertTrue(result);
                    result = false;
                }

                List<Metadata> list = metadataRequest.getList();
                Metadata tmp = list.get(0);
                for (int i = 1; i < list.size(); i++) {
                    assertTrue(tmp.getCreatedAt().getTime() >= list.get(i).getCreatedAt().getTime());
                    tmp = list.get(i);
                }
                providerBase.clearMetadata();
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    private void clearTestFolder() {
        FileUtils.purgeDirectory(new File(testFolder()));
        FileUtils.mkdirs(testFolder());
    }

    private void runTestMetadataQueryArgs(final String benchTag, final long totalCount, int perCount, final QueryArgs queryArgs, final BaseCallback callBack) {
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
            final MetadataRequest metadataRequest = new MetadataRequest(queryArgs);
            dataManager.submit(getContext(), metadataRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    assertNotNull(metadataRequest.getList());
                    assertTrue(metadataRequest.getList().size() <= queryArgs.limit);
                    if (metadataRequest.getCount() != totalCount) {
                        Log.e(benchTag, "count not matched: " + metadataRequest.getCount() +  "  " + totalCount);
                    }
                    assertTrue(metadataRequest.getCount() == totalCount);
                    if (!CollectionUtils.isNullOrEmpty(metadataRequest.getList())) {
                        BaseCallback.invoke(callBack, metadataRequest, e);
                    }
                    benchMark.report(benchTag + " count:" + totalCount + ",offset:" + offset + ",limit:" + limit);
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
        }
    }

    private void runTestAllBooksAndDescCreatedAt(final long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.allBooksQuery(defaultContentTypes(),
                OrderBy.fromProperty(Metadata_Table.createdAt).descending());
        runTestMetadataQueryArgs("####runTestAllBooksAndDescCreatedAt", totalCount, perCount, queryArgs, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                MetadataRequest metadataRequest = (MetadataRequest) request;
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestNewBooksAndAscSize(final long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.newBookListQuery(SortBy.Size, SortOrder.Asc);
        runTestMetadataQueryArgs("####runTestNewBooksAndAscSize", totalCount, perCount, queryArgs, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                MetadataRequest metadataRequest = (MetadataRequest) request;
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNull(metadata.getLastAccess());
                    assertTrue(tmp.getSize() <= metadata.getSize());
                    tmp = metadata;
                }
            }
        });
    }

    private void runTestReadingBooksAndDescName(final long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.recentReadingQuery(SortBy.Name, SortOrder.Desc);
        runTestMetadataQueryArgs("####runTestReadingBooksAndDescName", totalCount, perCount, queryArgs, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                MetadataRequest metadataRequest = (MetadataRequest) request;
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

    private void runTestReadedBooksAndDescAuthor(final long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.finishReadQuery(SortBy.Author, SortOrder.Desc);
        runTestMetadataQueryArgs("####runTestReadedBooksAndDescAuthor", totalCount, perCount, queryArgs, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                MetadataRequest metadataRequest = (MetadataRequest) request;
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

    private void runTestLibraryAllBooksAndCreatedAtDesc(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) {
        String libraryIdString = null;
        if (libraryIdSet != null && libraryIdSet.length > 0 && libraryIndex >= 0) {
            libraryIdString = libraryIdSet[libraryIndex];
        }
        final QueryArgs queryArgs = QueryBuilder.libraryAllBookQuery(libraryIdString,
                SortBy.CreationTime, SortOrder.Desc);
        runTestMetadataQueryArgs("####AllBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        MetadataRequest metadataRequest = (MetadataRequest) request;
                        Metadata tmp = metadataRequest.getList().get(0);
                        for (Metadata metadata : metadataRequest.getList()) {
                            assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
                            tmp = metadata;
                        }
                    }
                });
    }

    private void runTestLibraryNewBooksAndAscSize(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.libraryBookListNewQuery(libraryIdSet[libraryIndex],
                SortBy.Size, SortOrder.Asc);
        runTestMetadataQueryArgs("####NewBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        MetadataRequest metadataRequest = (MetadataRequest) request;
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

    private void runTestLibraryReadingBooksAndDescName(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.libraryRecentReadingQuery(libraryIdSet[libraryIndex],
                SortBy.Name, SortOrder.Desc);
        runTestMetadataQueryArgs("####ReadingBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        MetadataRequest metadataRequest = (MetadataRequest) request;
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

    private void runTestLibraryReadedBooksAndDescRecentlyRead(String[] libraryIdSet, int libraryIndex, long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.libraryFinishReadQuery(libraryIdSet[libraryIndex],
                SortBy.RecentlyRead, SortOrder.Desc);
        runTestMetadataQueryArgs("####ReadedBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        MetadataRequest metadataRequest = (MetadataRequest) request;
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

    private void runTestLibraryTagsBooksAndDescFileType(String[] libraryIdSet, int libraryIndex, Set<String> tags,
                                                        long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.libraryTagsFilterQuery(libraryIdSet[libraryIndex],
                tags, SortBy.FileType, SortOrder.Desc);
        runTestMetadataQueryArgs("####TagsBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        MetadataRequest metadataRequest = (MetadataRequest) request;
                        Metadata tmp = metadataRequest.getList().get(0);
                        for (Metadata metadata : metadataRequest.getList()) {
                            assertNotNull(metadata.getType());
                            assertTrue(tmp.getType().compareTo(metadata.getType()) >= 0);
                            tmp = metadata;
                        }
                    }
                });
    }

    private void runTestLibrarySearchBooksAndDescTitle(String[] libraryIdSet, int libraryIndex, String search,
                                                       long totalCount, int perCount) {
        final QueryArgs queryArgs = QueryBuilder.librarySearchQuery(libraryIdSet[libraryIndex],
                search, SortBy.BookTitle, SortOrder.Desc);
        runTestMetadataQueryArgs("####SearchBooks,libraryIndex:" + libraryIndex, totalCount, perCount, queryArgs,
                new BaseCallback() {
                    @Override
                    public void done(BaseRequest request, Throwable e) {
                        MetadataRequest metadataRequest = (MetadataRequest) request;
                        Metadata tmp = metadataRequest.getList().get(0);
                        for (Metadata metadata : metadataRequest.getList()) {
                            assertNotNull(metadata.getTitle());
                            assertTrue(tmp.getTitle().compareTo(metadata.getTitle()) >= 0);
                            tmp = metadata;
                        }
                    }
                });
    }

    public void test00BookListWithoutLibraryRequest() {
        clearTestFolder();
        Debug.setDebug(true);

        final DataProviderBase providerBase = getProviderBaseAndClearTable();
        providerBase.clearMetadata();

        long total = 0;
        int totalReadingBookCount = 0;
        int totalFinishedBookCount = 0;

        for (int r = 0; r < 35; ++r) {
            final int limit = TestUtils.randInt(150, 155);
            for (int i = 0; i < limit; i++) {
                Metadata meta = getRandomMetadata();
                int side = TestUtils.randInt(100, 200);
                if (side >= 166) {
                    ++totalFinishedBookCount;
                    meta.setLastAccess(new Date(System.currentTimeMillis()));
                    meta.setReadingStatus(Metadata.ReadingStatus.FINISHED);
                    meta.setProgress("100/100");
                } else if (side >= 133) {
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

    // test query all
    public void test001BookListWithLibraryRequest() {
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
        int metadataCount = TestUtils.randInt(20, 40);
        for (int i = 0; i < metadataCount; ++i) {
            final Metadata metadata = getRandomMetadata();
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
            final MetadataRequest metadataRequest = new MetadataRequest(queryArgs);
            dataManager.submit(getContext(), metadataRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
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
            });
            awaitCountDownLatch(countDownLatch);
        }
    }

    private static int maxLevel = 2;

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

    private void loadRecursiveLibraryList(DataProviderBase providerBase, List<Library> resultList, String parentIdString) {
        List<Library> libraryList = providerBase.loadAllLibrary(parentIdString);
        if (CollectionUtils.isNullOrEmpty(libraryList)) {
            return;
        }
        resultList.addAll(libraryList);
        for (Library library : libraryList) {
            loadRecursiveLibraryList(providerBase, resultList, library.getIdString());
        }
    }

    public void test00LibraryIntegratedFunc() {
        maxLevel = 2;
        clearTestFolder();
        Debug.setDebug(true);

        final DataProviderBase providerBase = getProviderBaseAndClearTable();

        //get nestedIn Library
        Library topLibrary = getRandomLibrary();
        providerBase.addLibrary(topLibrary);
        final int libraryCount = getNestedLibrary(topLibrary.getIdString(), 0);
        Log.e("###totalLibraryCount", String.valueOf(libraryCount));
        List<Library> list = new ArrayList<>();
        loadRecursiveLibraryList(providerBase, list, topLibrary.getIdString());
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
            final int finishBookCount = 35;
            final int readingBookCount = 30;
            final int tagsBooKCount = 50;
            final int searchCount = 74;
            final int addToLibraryCount = TestUtils.randInt(limit - 8, limit - 2);
            final String search = "1234567890-=";
            int libraryIndex = TestUtils.randInt(0, libraryIdSet.length - 1);
            String libraryIdString = libraryIdSet[libraryIndex];
            int originCount = libraryBookCountMap.get(libraryIdString);
            libraryBookCountMap.put(libraryIdString, originCount + addToLibraryCount);
            int originFreq = libraryAddBookFreqMap.get(libraryIdString);
            libraryAddBookFreqMap.put(libraryIdString, originFreq + 1);
            for (int i = 0; i < limit; i++) {
                Metadata meta = getRandomMetadata();
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
                    int replaceIndex = TestUtils.randInt(0, title.length() - 1);
                    int endIndex = TestUtils.randInt(replaceIndex, title.length() - 1);
                    meta.setTitle(title.replaceAll(title.substring(replaceIndex, endIndex), search));
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
            runTestLibraryReadingBooksAndDescName(libraryIdSet, libraryIndex, libraryAddBookFreqMap.get(libraryIdString) * readingBookCount, limit);
            runTestLibraryReadedBooksAndDescRecentlyRead(libraryIdSet, libraryIndex, libraryAddBookFreqMap.get(libraryIdString) * finishBookCount, limit);
            runTestLibraryTagsBooksAndDescFileType(libraryIdSet, libraryIndex, getFormatTagSet(), libraryAddBookFreqMap.get(libraryIdString) * tagsBooKCount, limit);
            runTestLibrarySearchBooksAndDescTitle(libraryIdSet, libraryIndex, search, libraryAddBookFreqMap.get(libraryIdString) * searchCount, limit);
            Log.e("##LibraryIntegratedFunc", "round: " + r + " finished. ");
        }

        //test path list query
        final QueryArgs queryArgs = QueryBuilder.allBooksQuery(SortBy.Name, SortOrder.Desc);
        queryArgs.propertyList.add(Metadata_Table.nativeAbsolutePath);
        runTestMetadataQueryArgs("##OnlyPathListQuery", total, 100, queryArgs, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                MetadataRequest metadataRequest = (MetadataRequest) request;
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

    private void awaitCountDownLatch(CountDownLatch countDownLatch) {
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    public DataProviderBase getProviderBase() {
        init();
        return DataProviderManager.getRemoteDataProvider();
    }

    public DataProviderBase getProviderBaseAndClearTable() {
        init();
        DataProviderBase dataProviderBase = getProviderBase();
        dataProviderBase.clearMetadata();
        dataProviderBase.clearLibrary();
        dataProviderBase.clearMetadataCollection();
        return dataProviderBase;
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

    public void testAddLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);
        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNotNull(testLibrary);
        assertTrue(testLibrary.getIdString().equals(library.getIdString()));
    }

    public void testUpdateLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);
        library.setDescription(generateRandomUUID());
        providerBase.updateLibrary(library);
        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNotNull(testLibrary);
        assertTrue(testLibrary.getIdString().equals(library.getIdString()));
        assertTrue(testLibrary.getDescription().equals(library.getDescription()));
    }

    public void testDeleteLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);
        assertTrue(library.getId() > 0);
        providerBase.deleteLibrary(library);
        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNull(testLibrary);
    }

    public void testQueryLibrary() {
        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearLibrary();

        Library library = getRandomLibrary();
        providerBase.addLibrary(library);

        Library testLibrary = providerBase.loadLibrary(library.getIdString());
        assertNotNull(testLibrary);
        assertTrue(testLibrary.getIdString().equals(library.getIdString()));

        //test query
        String like = "%" + testLibrary.getDescription().substring(0, 8) + "%";
        List<Library> libraryList = new Select().from(Library.class)
                .where(Library_Table.description.like(like))
                .queryList();
        assertNotNull(libraryList);
        assertTrue(libraryList.size() > 0);

        providerBase.clearLibrary();
        final int count = 5;
        final Library[] librarySet = new Library[count];
        for (int i = 0; i < count; i++) {
            librarySet[i] = getRandomLibrary();
            providerBase.addLibrary(librarySet[i]);
        }
        libraryList = providerBase.loadAllLibrary(null);
        assertNotNull(libraryList);
        assertTrue(libraryList.size() >= count);

        librarySet[0].setParentUniqueId(generateRandomUUID());
        providerBase.updateLibrary(librarySet[0]);
        libraryList = providerBase.loadAllLibrary(librarySet[0].getParentUniqueId());
        assertNotNull(libraryList);
        assertTrue(libraryList.size() == 1);
    }

    public static String[] getAscString(int count) {
        char init = 'A';
        String str[] = new String[count];
        for (int i = 0; i < count; i++) {
            char character = (char) (init + i);
            str[i] = String.valueOf(Character.toString(character));
        }
        return str;
    }

    public void testMetadataCollection() {
        DataProviderBase providerBase = getProviderBaseAndClearTable();

        int metadataCount = 5;//must be less than collectionCount
        int libraryCount = 5;//must be less than collectionCount
        int collectionCount = 6;

        Metadata[] metadataList = getRandomMetadata(metadataCount);
        Library[] libraryList = getRandomLibrary(libraryCount);

        for (int i = 0; i < collectionCount; i++) {
            String md5 = metadataList[i % metadataCount].getIdString();
            String libraryId = libraryList[i % libraryCount].getIdString();
            MetadataCollection collection = new MetadataCollection();
            collection.setLibraryUniqueId(libraryId);
            collection.setDocumentUniqueId(md5);
            providerBase.addMetadataCollection(getContext(), collection);
        }
        for (int i = 0; i < libraryList.length; i++) {
            List<MetadataCollection> list = providerBase.loadMetadataCollection(getContext(), libraryList[i].getIdString());
            assertTrue(list.size() >= 1);
            if (i == 0) {
                assertTrue(list.size() >= 2);
            }
        }

        for (int i = 0; i < libraryList.length; i++) {
            providerBase.deleteMetadataCollection(getContext(), libraryList[i].getIdString(), metadataList[i].getIdString());
            int size = providerBase.loadMetadataCollection(getContext(), libraryList[i].getIdString()).size();
            assertTrue(size == 0);
        }
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

    public void testAddToLibraryRequest() {
        maxLevel = 2;
        Debug.setDebug(true);
        clearTestFolder();

        DataProviderBase providerBase = getProviderBaseAndClearTable();

        DataManager dataManager = new DataManager();

        Library topLibrary = new Library();
        int libraryCount = getNestedLibrary(topLibrary.getIdString(), 0);
        List<Library> list = new ArrayList<>();
        DataManagerHelper.loadLibraryRecursive(dataManager, list, topLibrary.getIdString());
        assertTrue(libraryCount == CollectionUtils.getSize(list));

        providerBase.addLibrary(topLibrary);
        list.add(0, topLibrary);
        libraryCount++;

        int metadataCount = TestUtils.randInt(300, 500);
        int readingCount = TestUtils.randInt(metadataCount / 3, metadataCount / 2);
        List<Metadata> metaList = new ArrayList<>();
        for (int i = 0; i < metadataCount; i++) {
            Metadata meta = getRandomMetadata();
            if (i < readingCount) {
                meta.setLastAccess(new Date());
                meta.setProgress("40/100");
                meta.setReadingStatus(Metadata.ReadingStatus.READING);
            }
            providerBase.saveMetadata(getContext(), meta);
            metaList.add(meta);
        }
        final Benchmark benchMark = new Benchmark();
        int from = 0;
        for (int i = 0; i <= 5; i++) {
            int to = getRandomInt(libraryCount - 1, from);
            final int r = i;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            benchMark.restart();
            MoveToLibraryRequest addToLibraryRequest = new MoveToLibraryRequest(list.get(from), list.get(to), metaList);
            dataManager.submit(getContext(), addToLibraryRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    countDownLatch.countDown();
                    benchMark.reportError("####testAddToLibraryRequest,round:" + r);
                }
            });
            awaitCountDownLatch(countDownLatch);
            from = to;
            runTestLibraryAllBooksAndCreatedAtDesc(new String[]{list.get(to).getIdString()}, 0, metaList.size(), metaList.size() / 10);
            runTestLibraryReadingBooksAndDescName(new String[]{list.get(to).getIdString()}, 0, readingCount, readingCount / 10);
        }
    }

    public void testDeleteLibraryRequest() {
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
                Metadata meta = getRandomMetadata();
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
            tmpList.add(0, parentLibrary);

            int count = 0;
            for (Library library : tmpList) {
                count += providerBase.loadMetadataCollection(getContext(), library.getIdString()).size();
            }
            QueryArgs args = QueryBuilder.libraryAllBookQuery(parentLibrary.getParentUniqueId(), SortBy.CreationTime, SortOrder.Desc);
            count += providerBase.count(getContext(), args);

            //calculate time when deleting
            final int calCount = count;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Benchmark benchMark = new Benchmark();
            LibraryDeleteRequest deleteRequest = new LibraryDeleteRequest(parentLibrary);
            dataManager.submit(getContext(), deleteRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    benchMark.reportError("##testDeleteLibraryRequest,metaCollectionCount:" + calCount);
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);

            //check metadata count after deleting
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

    public void testClearLibraryRequest() {
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
                Metadata meta = getRandomMetadata();
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
                @Override
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

    public void testLibraryRequest() {
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

        final Map<String, Integer> libraryMetaCountMap = new HashMap<>();
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            int metadataCount = TestUtils.randInt(120, 125);
            libraryMetaCountMap.put(library.getIdString(), metadataCount);
            for (int j = 0; j < metadataCount; j++) {
                Metadata meta = getRandomMetadata();
                providerBase.saveMetadata(getContext(), meta);
                MetadataCollection collection = MetadataCollection.create(meta.getIdString(), library.getIdString());
                providerBase.addMetadataCollection(getContext(), collection);
            }
        }

        for (int i = 0; i < libraryList.size(); i++) {
            final Library library = libraryList.get(i);
            final int index = i;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Benchmark benchMark = new Benchmark();
            final QueryArgs args = QueryBuilder.libraryAllBookQuery(library.getIdString(), SortBy.CreationTime, SortOrder.Desc);
            final LibraryLoadRequest libraryLoadRequest = new LibraryLoadRequest(args);
            dataManager.submit(getContext(), libraryLoadRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    benchMark.reportError("##testLibraryRequest,index:" + index);
                    List<Library> childLibraryList = libraryLoadRequest.getLibraryList();
                    if (!CollectionUtils.isNullOrEmpty(childLibraryList)) {
                        for (Library tmp : childLibraryList) {
                            assertEquals(tmp.getParentUniqueId(), library.getIdString());
                        }
                    }
                    assertTrue(libraryLoadRequest.getTotalCount() == libraryMetaCountMap.get(library.getIdString()));
                    Metadata tmp = libraryLoadRequest.getBookList().get(0);
                    for (Metadata metadata : libraryLoadRequest.getBookList()) {
                        assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
                        tmp = metadata;
                    }
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
        }
    }

    public void testRemoveLibraryRequest() {
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

        Map<String, Integer> libraryMetaCountMap = new HashMap<>();
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            int metadataCount = TestUtils.randInt(150, 155);
            libraryMetaCountMap.put(library.getIdString(), metadataCount);
            for (int j = 0; j < metadataCount; j++) {
                Metadata meta = getRandomMetadata();
                providerBase.saveMetadata(getContext(), meta);
                MetadataCollection collection = MetadataCollection.create(meta.getIdString(), library.getIdString());
                providerBase.addMetadataCollection(getContext(), collection);
            }
        }

        int topTotalCount = 0;
        for (int i = 0; i < libraryList.size(); i++) {
            Library library = libraryList.get(i);
            int limit = TestUtils.randInt(145, 150);
            topTotalCount += limit;
            QueryArgs args = QueryBuilder.libraryAllBookQuery(library.getIdString(), SortBy.CreationTime, SortOrder.Desc);
            args.limit = limit;
            List<Metadata> list = dataManager.getRemoteContentProvider().findMetadataByQueryArgs(getContext(), args);
            final int index = i;
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final Benchmark benchMark = new Benchmark();
            RemoveFromLibraryRequest removeRequest = new RemoveFromLibraryRequest(library, list);
            dataManager.submit(getContext(), removeRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    benchMark.reportError("##testRemoveFromLibraryRequest,index:" + index);
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

    private Set<String> getRandomStringSet(int count) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < count; i++) {
            set.add(generateRandomUUID());
        }
        return set;
    }

    private String getRandomString(Set<String> set) {
        List<String> list = new ArrayList<>();
        list.addAll(set);
        int index = TestUtils.randInt(0, list.size() - 1);
        return list.get(index);
    }

    public void testBuildLibraryRequest() {
        Debug.setDebug(true);
        clearTestFolder();

        DataProviderBase providerBase = getProviderBaseAndClearTable();

        Set<String> seriesSet = getRandomStringSet(10);
        Set<String> tagSet = getFormatTagSet();
        Set<String> titleSet = getRandomStringSet(10);
        Set<String> authorSet = getRandomStringSet(10);

        int typeSetCount = TestUtils.randInt(0, 10);
        int seriesSetCount = TestUtils.randInt(typeSetCount, 20);
        int tagSetCount = TestUtils.randInt(seriesSetCount, 30);
        int titleSetCount = TestUtils.randInt(tagSetCount, 40);
        int authorCount = TestUtils.randInt(titleSetCount, 50);

        int count = TestUtils.randInt(150, 180);
        for (int i = 0; i < count; i++) {
            Metadata meta = getRandomMetadata();
            meta.setType(null);
            if (i < typeSetCount) {
                meta.setType(TestUtils.randomType());
            }
            if (i < seriesSetCount) {
                meta.setSeries(getRandomString(seriesSet));
            }
            if (i < tagSetCount) {
                meta.setTags(getRandomString(tagSet));
            }
            if (i < titleSetCount) {
                meta.setTitle(getRandomString(titleSet));
            }
            if (i < authorCount) {
                meta.setAuthors(getRandomString(authorSet));
            }
            providerBase.saveMetadata(getContext(), meta);
        }

        //test fileType
        Library library = getRandomLibrary();
        QueryArgs args = new QueryArgs();
        args.fileType = TestUtils.defaultContentTypes();
        runBuildLibraryAndCompare(library, args, typeSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        //test tags
        args = new QueryArgs();
        args.tags = tagSet;
        runBuildLibraryAndCompare(library, args, tagSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        //test series
        args = new QueryArgs();
        args.series = seriesSet;
        runBuildLibraryAndCompare(library, args, seriesSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        //test title
        args = new QueryArgs();
        args.title = titleSet;
        runBuildLibraryAndCompare(library, args, titleSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        //test author
        args = new QueryArgs();
        args.author = authorSet;
        runBuildLibraryAndCompare(library, args, authorCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        args = new QueryArgs();
        args.title = titleSet;
        args.author = authorSet;
        runBuildLibraryAndCompare(library, args, titleSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        args = new QueryArgs();
        args.tags = tagSet;
        args.title = titleSet;
        args.author = authorSet;
        runBuildLibraryAndCompare(library, args, tagSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        args = new QueryArgs();
        args.series = seriesSet;
        args.tags = tagSet;
        args.title = titleSet;
        args.author = authorSet;
        runBuildLibraryAndCompare(library, args, seriesSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());

        args = new QueryArgs();
        args.fileType = TestUtils.defaultContentTypes();
        args.series = seriesSet;
        args.tags = tagSet;
        args.title = titleSet;
        args.author = authorSet;
        runBuildLibraryAndCompare(library, args, typeSetCount);
        providerBase.deleteMetadataCollection(getContext(), library.getIdString());
    }

    private void runBuildLibraryAndCompare(final Library library, QueryArgs args, final int compareCount) {
        DataManager dataManager = new DataManager();
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final LibraryBuildRequest buildRequest = new LibraryBuildRequest(library, args);
        dataManager.submit(getContext(), buildRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertEquals(CollectionUtils.getSize(buildRequest.getBookList()), compareCount);
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
        runTestLibraryAllBooksAndCreatedAtDesc(new String[]{library.getIdString()}, 0, compareCount, compareCount / 10);
    }
}

