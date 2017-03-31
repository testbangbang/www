package com.onyx.android.sdk;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.DataCacheManager;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Library_Table;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.MetadataCollection_Table;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.model.ReadingProgress;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.MetadataRequest;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhuzeng on 8/26/16.
 */
public class MetadataTest extends ApplicationTestCase<Application> {

    private static boolean dbInit = false;

    public MetadataTest() {
        super(Application.class);
    }

    public void init() {
        if (dbInit) {
            return;
        }
        dbInit = true;
        DataManager.init(getContext(), null);
    }

    public static Set<String> defaultContentTypes() {
        Set<String> defaultTypes = null;
        if (defaultTypes == null) {
            defaultTypes = new HashSet<String>();
            defaultTypes.add("epub");
            defaultTypes.add("pdf");
            defaultTypes.add("mobi");
            defaultTypes.add("prc");
            defaultTypes.add("rtf");
            defaultTypes.add("doc");
            defaultTypes.add("fb2");
            defaultTypes.add("txt");
            defaultTypes.add("docx");
            defaultTypes.add("chm");
            defaultTypes.add("djvu");
            defaultTypes.add("azw3");
            defaultTypes.add("zip");
        }
        return defaultTypes;
    }

    public static File generateRandomFolder(final String parent) {
        File dir = new File(parent);
        dir.mkdirs();

        File childFolder = new File(parent, Metadata.generateUniqueId());
        childFolder.mkdirs();
        return childFolder;
    }

    public static File generateRandomFile(final String parent, boolean hasExtension) {
        File dir = new File(parent);
        dir.mkdirs();

        final String ext;
        if (hasExtension) {
            ext = "." + randomType();
        } else {
            ext = "";
        }
        File file = new File(parent, UUID.randomUUID().toString() + ext);
        StringBuilder builder = new StringBuilder();
        int limit = TestUtils.randInt(100, 1024);
        for (int i = 0; i < limit; ++i) {
            builder.append(UUID.randomUUID().toString());
        }
        FileUtils.saveContentToFile(builder.toString(), file);
        return file;
    }

    public static String randomType() {
        List<String> list = new ArrayList<String>();
        list.addAll(defaultContentTypes());
        int index = TestUtils.randInt(0, list.size() - 1);
        return list.get(index);
    }

    public static List<String> randomStringList() {
        int value = TestUtils.randInt(1, 5);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < value; ++i) {
            list.add(UUID.randomUUID().toString());
        }
        return list;
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
            String md5 = FileUtils.computeMD5(file);
            metadata.setIdString(md5);
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
        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

        List<String> authors = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        final Metadata result = dataProvider.findMetadata(getContext(), origin.getNativeAbsolutePath(), origin.getIdString());
        assertNotNull(result);
        assertEquals(result.getIdString(), origin.getIdString());

        dataProvider.removeMetadata(getContext(), origin);
        final Metadata anotherResult = dataProvider.findMetadata(getContext(), origin.getNativeAbsolutePath(), origin.getIdString());
        assertNull(anotherResult);
    }

    public void testMetadataSaveWithJson() {
        init();
        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

        final String json = UUID.randomUUID().toString();
        Metadata origin = randomMetadata(testFolder(), true);
        dataProvider.saveDocumentOptions(getContext(), origin.getNativeAbsolutePath(), origin.getIdString(), json);

        final Metadata result = dataProvider.findMetadata(getContext(), origin.getNativeAbsolutePath(), origin.getIdString());
        assertNotNull(result);
        assertEquals(result.getIdString(), origin.getIdString());
        assertEquals(result.getExtraAttributes(), json);
    }

    public void testQueryCriteriaAuthor() {
        init();

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();
        dataProvider.clearMetadataCollection();

        List<String> authors = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = randomMetadata(testFolder(), true);
            dataProvider.saveMetadata(getContext(), metadata);
        }

        QueryArgs args = MetadataQueryArgsBuilder.allBooksQuery(defaultContentTypes(), SortBy.Name, SortOrder.Desc);
        List<Metadata> testList = dataProvider.findMetadata(getContext(), args);
        assertNotNull(testList);
        assertTrue(testList.size() > 0);

        final QueryArgs queryCriteria = new QueryArgs();
        queryCriteria.author.addAll(authors);
        MetadataQueryArgsBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == limit + 1);
    }

    public void testQueryCriteriaTitle() {
        init();

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

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
        MetadataQueryArgsBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == limit + 1);
    }

    public void testQueryCriteriaSeries() {
        init();

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

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
        MetadataQueryArgsBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == limit + 1);
    }

    public void testQueryCriteriaTags() {
        init();

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

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
        MetadataQueryArgsBuilder.generateCriteriaCondition(queryCriteria);

        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryArgs dummyQueryCriteria = new QueryArgs();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
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
        final Metadata result = dataProvider.findMetadata(context, origin.getNativeAbsolutePath(), origin.getIdString());
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
        final DataProviderBase providerBase = DataProviderManager.getDataProvider();
        final String indexTag = getRandomFormatTag();
        providerBase.clearMetadata();
        final Metadata[] testMetadata = getRandomMetadata(count);
        for (int i = 0; i < count; i++) {
            testMetadata[i].setTags(getRandomFormatTag());
            if (i % divider == 0) {
                testMetadata[i].setTags(indexTag);
            }
            testMetadata[i].save();
            assertRandomMetadata(getContext(), providerBase, testMetadata[i]);
        }

        Set<String> tagSet = new HashSet<>();
        tagSet.add(indexTag);

        QueryArgs queryArgs = MetadataQueryArgsBuilder.tagsFilterQuery(tagSet, SortBy.Name, SortOrder.Desc);
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
        final DataProviderBase providerBase = DataProviderManager.getDataProvider();
        providerBase.clearMetadata();
        final Metadata[] testMetadata = new Metadata[count];
        for (int i = 0; i < count; i++) {
            testMetadata[i] = getRandomMetadata();
            testMetadata[i].save();
            assertRandomMetadata(getContext(), providerBase, testMetadata[i]);
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        QueryArgs queryArgs = MetadataQueryArgsBuilder.allBooksQuery(defaultContentTypes(),
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
        deleteRecursive(testFolder());
    }

    private void deleteRecursive(final String path) {
        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) {
            }
        }
    }

    private void runTestMetadataQueryArgs(final String benchTag, final long totalCount, int perCount, final QueryArgs queryArgs, final BaseCallback callBack) {
        final Benchmark benchMark = new Benchmark();
        DataManager dataManager = new DataManager();
        int limit = 10;
        queryArgs.limit = limit;
        for (int offset = 0; offset < perCount / limit; ++offset) {
            benchMark.restart();
            final CountDownLatch countDownLatch = new CountDownLatch(1);
            final int round = offset;
            queryArgs.offset = offset;
            final MetadataRequest metadataRequest = new MetadataRequest(queryArgs);
            dataManager.submit(getContext(), metadataRequest, new BaseCallback() {
                @Override
                public void done(BaseRequest request, Throwable e) {
                    assertNull(e);
                    assertNotNull(metadataRequest.getList());
                    assertTrue(metadataRequest.getList().size() <= queryArgs.limit);
                    assertTrue(metadataRequest.getCount() == totalCount);
                    if (!CollectionUtils.isNullOrEmpty(metadataRequest.getList())) {
                        BaseCallback.invoke(callBack, metadataRequest, e);
                    }
                    benchMark.report(benchTag + " count:" + totalCount + ",offset:" + round);
                    countDownLatch.countDown();
                }
            });
            awaitCountDownLatch(countDownLatch);
        }
    }

    private void runTestAllBooksAndDescCreatedAt(final long totalCount, int perCount) {
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.allBooksQuery(defaultContentTypes(),
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.newBookListQuery(SortBy.Size, SortOrder.Asc);
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.recentReadingQuery(SortBy.Name, SortOrder.Desc);
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.finishReadQuery(SortBy.Author, SortOrder.Desc);
        runTestMetadataQueryArgs("####runTestReadedBooksAndDescAuthor", totalCount, perCount, queryArgs, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                MetadataRequest metadataRequest = (MetadataRequest) request;
                Metadata tmp = metadataRequest.getList().get(0);
                for (Metadata metadata : metadataRequest.getList()) {
                    assertNotNull(metadata.getProgress());
                    assertNotNull(metadata.getLastAccess());
                    assertTrue(metadata.isReaded());
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.libraryAllBookQuery(libraryIdString,
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.libraryNewBookListQuery(libraryIdSet[libraryIndex],
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.libraryRecentReadingQuery(libraryIdSet[libraryIndex],
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.libraryFinishReadQuery(libraryIdSet[libraryIndex],
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.libraryTagsFilterQuery(libraryIdSet[libraryIndex],
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
        final QueryArgs queryArgs = MetadataQueryArgsBuilder.librarySearchQuery(libraryIdSet[libraryIndex],
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

    public void test00BookListPaginationRequest() {
        clearTestFolder();
        Debug.setDebug(true);

        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearMetadata();

        long total = 0;

        for (int r = 0; r < 100; ++r) {
            final int limit = TestUtils.randInt(100, 150);
            final int newBookCount = 45;
            final int finishBookCount = 55;
            for (int i = 0; i < limit; i++) {
                Metadata meta = getRandomMetadata();
                meta.setLastAccess(new Date(System.currentTimeMillis()));
                if (i < newBookCount) {
                    meta.setLastAccess(null);
                }
                if (i >= newBookCount && i < newBookCount + finishBookCount) {
                    meta.setProgress("100/100");
                }
                meta.save();
            }
            total += limit;
            final long value = total;
            Log.e("####BookListPagination", "record generated: " + limit);
            runTestAllBooksAndDescCreatedAt(value, limit);
            runTestNewBooksAndAscSize(newBookCount * (r + 1), limit);
            runTestReadingBooksAndDescName(finishBookCount * (r + 1), limit);
            runTestReadedBooksAndDescAuthor(finishBookCount * (r + 1), limit);
            Log.e("####BookListPagination", "round: " + r + " finished. ");
        }
    }

    private static int currentFloor = 0;

    private int getNestedLibrary(String parentIdString) {
        if (currentFloor != 0 && currentFloor % 3 == 0) {//most 3
            return 0;
        }
        currentFloor++;
        int total = 0;
        int randomCount = TestUtils.randInt(4, 7);
        total += randomCount;
        Library[] libraries = getRandomLibrary(randomCount);
        for (Library library : libraries) {
            library.setParentUniqueId(parentIdString);
            library.save();
            total += getNestedLibrary(library.getIdString());
        }
        Log.e("###perFloorCount:", String.valueOf(randomCount));
        return total;
    }

    private List<Library> loadRecursiveLibraryList(DataProviderBase providerBase, String parentIdString) {
        List<Library> libraryList = new ArrayList<>();
        List<Library> libraries = providerBase.loadAllLibrary(parentIdString);
        if (!CollectionUtils.isNullOrEmpty(libraries)) {
            libraryList.addAll(libraries);
            for (Library library : libraries) {
                libraryList.addAll(loadRecursiveLibraryList(providerBase, library.getIdString()));
            }
        }
        return libraryList;
    }

    public void test00LibraryIntegratedFunc() {
        currentFloor = 0;
        clearTestFolder();
        Debug.setDebug(true);

        final DataProviderBase providerBase = getProviderBase();
        providerBase.clearMetadata();
        providerBase.clearLibrary();
        providerBase.clearMetadataCollection();

        //get nestedIn Library
        Library topLibrary = getRandomLibrary();
        topLibrary.save();
        final int libraryCount = getNestedLibrary(topLibrary.getIdString());
        Log.e("###totalLibraryCount", String.valueOf(libraryCount));
        List<Library> list = loadRecursiveLibraryList(providerBase, topLibrary.getIdString());
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
        for (int r = 0; r < 100; ++r) {
            final int limit = TestUtils.randInt(100, 120);
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
                    meta.setReadingStatus(Metadata.ReadingStatus.READ_DONE);
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
                meta.save();

                if (i < addToLibraryCount) {
                    MetadataCollection metadataCollection = new MetadataCollection();
                    metadataCollection.setLibraryUniqueId(libraryIdString);
                    metadataCollection.setDocumentUniqueId(meta.getNativeAbsolutePath());
                    metadataCollection.save();
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
        final QueryArgs queryArgs = new QueryArgs();
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
            SQLite.update(MetadataCollection.class)
                    .set(MetadataCollection_Table.libraryUniqueId.eq(topLibrary.getParentUniqueId()))
                    .where(MetadataCollection_Table.libraryUniqueId.is(library.getIdString()))
                    .execute();
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
        return DataProviderManager.getDataProvider();
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

    public static Bitmap getSquareBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(707, 707, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(0xFFAA00AA);
        return bitmap;
    }

    public List<Thumbnail> testSaveThumbnail() {
        DataProviderBase providerBase = getProviderBase();
        providerBase.clearThumbnail();

        String md5 = generateRandomUUID().substring(0, 12);
        providerBase.addThumbnail(getContext(), md5, getSquareBitmap());

        List<Thumbnail> list = providerBase.loadThumbnail(getContext(), md5);
        assertNotNull(list);
        assertTrue(list.size() > 0);
        assertTrue(list.get(0).getSourceMD5().equals(md5));
        Bitmap tmp = getSquareBitmap();
        for (Thumbnail thumbnail : list) {
            Bitmap bitmap = providerBase.loadThumbnailBitmap(getContext(), thumbnail.getSourceMD5(), thumbnail.getThumbnailKind());
            assertNotNull(bitmap);
            assertTrue(tmp.getByteCount() >= bitmap.getByteCount());
            tmp = bitmap;
        }
        return list;
    }

    public void testUpdateThumbnail() {
        DataProviderBase providerBase = getProviderBase();
        providerBase.clearThumbnail();

        Thumbnail thumbnail = testSaveThumbnail().get(0);
        thumbnail.setSourceMD5(getRandomFormatTag());
        providerBase.updateThumbnail(thumbnail);
        List<Thumbnail> list = providerBase.loadThumbnail(getContext(), thumbnail.getSourceMD5());
        assertNotNull(list);
        assertTrue(list.size() == 1);
        assertTrue(list.get(0).getSourceMD5().equals(thumbnail.getSourceMD5()));
    }

    public void testDeleteThumbnail() {
        DataProviderBase providerBase = getProviderBase();
        providerBase.clearThumbnail();

        List<Thumbnail> list = testSaveThumbnail();
        providerBase.deleteThumbnail(list.get(0));
        list = providerBase.loadThumbnail(getContext(), list.get(0).getSourceMD5());
        assertNotNull(list);
        assertTrue(list.size() == OnyxThumbnail.ThumbnailKind.values().length - 1);
        providerBase.clearThumbnail();
        list = providerBase.loadThumbnail(getContext(), list.get(0).getSourceMD5());
        assertNotNull(list);
        assertTrue(list.size() == 0);
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

    /**
     * test findMetadata(final Context context, final QueryArgs queryArgs);
     */
    public void testMetadataInQuery() {
        DataProviderBase providerBase = getProviderBase();
        providerBase.clearMetadata();
        providerBase.clearMetadataCollection();

        int count = 10;
        String[] ascString = getAscString(count);
        String[] tags = getFormatTags();
        String collectionLibraryUid = generateRandomUUID();
        String testSearch = "UUID";
        String testTag = tags[0];
        for (int i = 0; i < count; i++) {
            Metadata metadata = getRandomMetadata();
            metadata.setName(ascString[i]);
            metadata.setTitle(ascString[i]);
            if (i == 1) {
                metadata.setProgress("12/33");
                metadata.setLastAccess(new Date());
            }
            if (i == 2) {
                metadata.setProgress("14/33");
                metadata.setLastAccess(new Date());
            }
            if (i == 3) {
                metadata.setProgress("12/12");
                metadata.setLastAccess(new Date());
            }

            MetadataCollection collection = new MetadataCollection();
            if (i >= 4 && i < 6) {//count 2
                metadata.setName(testSearch);// use name to test search
                collection.setLibraryUniqueId(collectionLibraryUid);//one can't do without the other
            }
            if (i == 6) {
                collection.setLibraryUniqueId(collectionLibraryUid);
                metadata.setLastAccess(new Date());
                metadata.setProgress("12/15");
                metadata.setTags(testTag);
            }
            collection.setIdString(generateRandomUUID());
            collection.setDocumentUniqueId(metadata.getIdString());
            collection.save();
            metadata.save();
        }

        // test sortBy name desc and load
        QueryArgs args = MetadataQueryArgsBuilder.libraryAllBookQuery(null, defaultContentTypes(), MetadataQueryArgsBuilder.getOrderByName().descending());
        List<Metadata> list = providerBase.findMetadata(getContext(), args);
        int j = 0;
        Metadata tmp = list.get(j);
        for (int i = list.size() - 1; i >= 0; i--, j++) {
            Metadata metadata = list.get(j);
            int result = MetaDataUtils.compareStringAsc(tmp.getName(), metadata.getName());
            assertTrue(result >= 0);
            tmp = metadata;
        }

        // test metadata belong to library
        args = MetadataQueryArgsBuilder.libraryAllBookQuery(collectionLibraryUid, defaultContentTypes(), MetadataQueryArgsBuilder.getOrderByName().descending());
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 3);

        // test new book
        args = MetadataQueryArgsBuilder.libraryNewBookListQuery(collectionLibraryUid, SortBy.Name, SortOrder.Desc);
        args.libraryUniqueId = collectionLibraryUid;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 2);

        //test recent reading and OrderBy updatedAt desc
        args = MetadataQueryArgsBuilder.libraryRecentReadingQuery(collectionLibraryUid, SortBy.RecentlyRead, SortOrder.Desc);
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 1);
        tmp = list.get(0);
        for (int i = 0; i < list.size(); i++) {
            assertTrue(tmp.getLastAccess().getTime() >= list.get(i).getLastAccess().getTime());
            tmp = list.get(i);
        }

        //test recent reading and has libraryUniqueId
        args.libraryUniqueId = collectionLibraryUid;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 1);

        //test recentAdd
        args = MetadataQueryArgsBuilder.recentAddQuery();
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 6);

        //test recentAdd and has libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryRecentAddQuery(collectionLibraryUid, SortBy.RecentlyRead, SortOrder.Desc);
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 2);

        args = MetadataQueryArgsBuilder.finishReadQuery(SortBy.RecentlyRead, SortOrder.Desc);
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() > 0);
        list = MetaDataUtils.verifyReadedStatus(list, BookFilter.READED);
        assertTrue(list.size() == 1);

        //test finish read with libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryFinishReadQuery(collectionLibraryUid, SortBy.RecentlyRead, SortOrder.Desc);
        list = providerBase.findMetadata(getContext(), args);
        list = MetaDataUtils.verifyReadedStatus(list, BookFilter.READED);
        assertTrue(list.size() == 0);

        // test search with libraryUniqueId
        args = MetadataQueryArgsBuilder.librarySearchQuery(collectionLibraryUid, testSearch, SortBy.Name, SortOrder.Desc);
        args.query = testSearch;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 2);
        assertEquals(list.get(0).getName(), list.get(1).getName());

        args = MetadataQueryArgsBuilder.libraryTagsFilterQuery(collectionLibraryUid, getFormatTagSet(), SortBy.Name, SortOrder.Desc);
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 1);
        assertEquals(list.get(0).getTags(), testTag);
    }

    public void testMetadataCollection() {
        DataProviderBase providerBase = getProviderBase();
        providerBase.clearMetadata();
        providerBase.clearLibrary();
        Delete.table(MetadataCollection.class);

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

    public void testLibraryMetadataCache() {
        init();
        DataManager dataManager = new DataManager();
        DataCacheManager cacheManager = dataManager.getDataCacheManager();
        DataProviderBase providerBase = dataManager.getDataProviderBase();
        providerBase.clearMetadata();
        providerBase.clearLibrary();
        providerBase.clearMetadataCollection();

        int count = TestUtils.randInt(10, 20);
        String[] ascString = getAscString(count);
        String libraryUniqueId = generateRandomUUID();
        Metadata[] metadataList = getRandomMetadata(count);
        for (int i = 0; i < count; i++) {
            metadataList[i].setName(ascString[i]);
            if (i == 0) {
                metadataList[i].setProgress("12/12");
                metadataList[i].setLastAccess(new Date());
            }

            if (i == 1) {
                metadataList[i].setProgress("12/33");
                metadataList[i].setLastAccess(new Date());
            }

            if (i > 1 && i < 5) { //3
                MetadataCollection collection = new MetadataCollection();
                collection.setLibraryUniqueId(libraryUniqueId);
                collection.setDocumentUniqueId(metadataList[i].getIdString());
                collection.save();
                if (i == 2) {
                    metadataList[i].setLastAccess(new Date());
                }
                if (i == 4) {
                    metadataList[i].setTags(getRandomFormatTag());
                }
            }

            metadataList[i].save();
        }

        //test all and desc by Name
        QueryArgs args = MetadataQueryArgsBuilder.libraryAllBookQuery(null, SortBy.Name, SortOrder.Desc);
        List<Metadata> list = dataManager.getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() >= metadataList.length - 3);
        Metadata tmp = list.get(0);
        for (int i = list.size() - 1, j = 0; i >= 0; i--, j++) {
            Metadata metadata = list.get(j);
            int result = MetaDataUtils.compareStringAsc(tmp.getName(), metadata.getName());
            assertTrue(result >= 0);
            tmp = metadata;
        }

        // test cache
        LibraryCache libraryCache = cacheManager.getLibraryCache(null);
        assertEquals(libraryCache.getIdList().size(), list.size());
        for (Metadata metadata : list) {
            assertTrue(libraryCache.getIdList().contains(metadata.getIdString()));
            assertEquals(cacheManager.getById(metadata.getIdString()), metadata);
        }

        //test reading and desc by Name
        args = MetadataQueryArgsBuilder.libraryRecentReadingQuery(null, SortBy.Name, SortOrder.Desc);
        list = dataManager.getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 2);

        //test finish read and desc by Name
        args = MetadataQueryArgsBuilder.libraryFinishReadQuery(null, SortBy.Name, SortOrder.Desc);
        list = dataManager.getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 1);

        //test all has libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryAllBookQuery(libraryUniqueId, SortBy.Name, SortOrder.Desc);
        list = dataManager.getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 3);
        assertTrue(cacheManager.getLibraryCache(libraryUniqueId).getIdList().size() == 3);

        //test newBook has libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryNewBookListQuery(libraryUniqueId, SortBy.Name, SortOrder.Desc);
        list = dataManager.getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 2);

        //test tag has libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryTagsFilterQuery(libraryUniqueId, getFormatTagSet(), SortBy.Name, SortOrder.Desc);
        list = dataManager.getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 1);
        List<String> md5List = cacheManager.getLibraryCache(libraryUniqueId).getIdList();
        assertTrue(md5List.contains(list.get(0).getIdString()));
        assertNotNull(cacheManager.getById(md5List.get(0)));
    }
}

