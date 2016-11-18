package com.onyx.android.sdk;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.cache.DataCacheManager;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Library_Table;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.MetadataRequest;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.*;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.MetadataTestUtils.awaitCountDownLatch;
import static com.onyx.android.sdk.MetadataTestUtils.getAscString;
import static com.onyx.android.sdk.MetadataTestUtils.getRandomLibrary;

/**
 * Created by zhuzeng on 8/26/16.
 */
public class MetadataBaseTest extends ApplicationTestCase<Application> {


    public MetadataBaseTest() {
        super(Application.class);
    }


    // simple metadata save and trieve test.
    public void testMetadataSave() {
        MetadataTestUtils.init(getContext());
        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

        List<String> authors = MetadataTestUtils.randomStringList();
        Metadata origin = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        final Metadata result = dataProvider.findMetadata(getContext(), origin.getNativeAbsolutePath(), origin.getIdString());
        assertNotNull(result);
        assertEquals(result.getIdString(), origin.getIdString());

        dataProvider.removeMetadata(getContext(), origin);
        final Metadata anotherResult = dataProvider.findMetadata(getContext(), origin.getNativeAbsolutePath(), origin.getIdString());
        assertNull(anotherResult);
    }

    // simple test with json options.
    public void testMetadataSaveWithJson() {
        MetadataTestUtils.init(getContext());
        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

        final String json = UUID.randomUUID().toString();
        Metadata origin = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
        dataProvider.saveDocumentOptions(getContext(), origin.getNativeAbsolutePath(), origin.getIdString(), json);

        final Metadata result = dataProvider.findMetadata(getContext(), origin.getNativeAbsolutePath(), origin.getIdString());
        assertNotNull(result);
        assertEquals(result.getIdString(), origin.getIdString());
        assertEquals(result.getExtraAttributes(), json);
    }

    public void testQueryCriteriaAuthor() {
        MetadataTestUtils.init(getContext());

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();
        dataProvider.clearMetadataCollection();

        List<String> authors = MetadataTestUtils.randomStringList();
        Metadata origin = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
            dataProvider.saveMetadata(getContext(), metadata);
        }

        QueryArgs args = MetadataQueryArgsBuilder.allBooksQuery(MetadataTestUtils.defaultContentTypes(), SortBy.Name, SortOrder.Desc);
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

    // query with title
    public void testQueryCriteriaTitle() {
        MetadataTestUtils.init(getContext());

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

        List<String> title = MetadataTestUtils.randomStringList();
        Metadata origin = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
        origin.setTitle(StringUtils.join(title, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
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

    // query with series
    public void testQueryCriteriaSeries() {
        MetadataTestUtils.init(getContext());

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

        List<String> series = MetadataTestUtils.randomStringList();
        Metadata origin = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
        origin.setSeries(StringUtils.join(series, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
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

    // query with tags
    public void testQueryCriteriaTags() {
        MetadataTestUtils.init(getContext());

        LocalDataProvider dataProvider = new LocalDataProvider();
        dataProvider.clearMetadata();

        List<String> tags = MetadataTestUtils.randomStringList();
        Metadata origin = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
        origin.setTags(StringUtils.join(tags, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = MetadataTestUtils.randomMetadata(MetadataTestUtils.testFolder(), true);
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

    public void testTagsFilterRequest() {
        MetadataTestUtils.init(getContext());

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final int count = 12;//at least 4
        final int divider = 3; // to be smaller than count
        final DataProviderBase providerBase = DataProviderManager.getDataProvider();
        final String indexTag = MetadataTestUtils.getRandomFormatTag();
        providerBase.clearMetadata();
        final HashMap<String, Metadata> originMetadata = MetadataTestUtils.getRandomMetadata(count);
        int i = 0;
        for (Map.Entry<String, Metadata> entry: originMetadata.entrySet()) {
            entry.getValue().setTags(MetadataTestUtils.getRandomFormatTag());
            if (i % divider == 0) {
                entry.getValue().setTags(indexTag);
            }
            entry.getValue().save();
            ++i;
            MetadataTestUtils.assertRandomMetadata(getContext(), providerBase, entry.getValue());
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
                for (Metadata metadata : metadataRequest.getList()) {
                    assertTrue(originMetadata.containsKey(metadata.getIdString()));
                }

                providerBase.clearMetadata();
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    public void testBookListRequest() {
        MetadataTestUtils.init(getContext());

        final int count = TestUtils.randInt(4, 10);
        final DataProviderBase provider = DataProviderManager.getDataProvider();
        provider.clearMetadata();
        final HashMap<String, Metadata> originMetadataMap = new HashMap<>();
        for (int i = 0; i < count; i++) {
            final Metadata metadata = MetadataTestUtils.getRandomMetadata();
            originMetadataMap.put(metadata.getIdString(), metadata);
            metadata.save();
            MetadataTestUtils.assertRandomMetadata(getContext(), provider, metadata);
        }

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        QueryArgs queryArgs = MetadataQueryArgsBuilder.allBooksQuery(MetadataTestUtils.defaultContentTypes(),
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
                for (Metadata metadata : metadataRequest.getList()) {
                    assertTrue(originMetadataMap.containsKey(metadata.getIdString()));
                }

                List<Metadata> list = metadataRequest.getList();
                Metadata tmp = list.get(0);
                for (int i = 1; i < list.size(); i++) {
                    assertTrue(tmp.getCreatedAt().getTime() >= list.get(i).getCreatedAt().getTime());
                    tmp = list.get(i);
                }
                provider.clearMetadata();
                countDownLatch.countDown();
            }
        });
        awaitCountDownLatch(countDownLatch);
    }

    public DataProviderBase getProviderBase() {
        MetadataTestUtils.init(getContext());
        return DataProviderManager.getDataProvider();
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

        String md5 = MetadataTestUtils.generateRandomUUID().substring(0, 12);
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
        thumbnail.setSourceMD5(MetadataTestUtils.getRandomFormatTag());
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

    /**
     * test findMetadata(final Context context, final QueryArgs queryArgs);
     */
    public void testMetadataInQuery() {
        DataProviderBase providerBase = getProviderBase();
        providerBase.clearMetadata();
        providerBase.clearMetadataCollection();

        int count = 10;
        String[] ascString = getAscString(count);
        String[] tags = MetadataTestUtils.getFormatTags();
        String collectionLibraryUid = MetadataTestUtils.generateRandomUUID();
        String testSearch = "UUID";
        String testTag = tags[0];
        for (int i = 0; i < count; i++) {
            Metadata metadata = MetadataTestUtils.getRandomMetadata();
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
            collection.setIdString(MetadataTestUtils.generateRandomUUID());
            collection.setDocumentUniqueId(metadata.getIdString());
            collection.save();
            metadata.save();
        }

        // test sortBy name desc and load
        QueryArgs args = MetadataQueryArgsBuilder.libraryAllBookQuery(null, MetadataTestUtils.defaultContentTypes(), MetadataQueryArgsBuilder.getOrderByName().descending());
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
        args = MetadataQueryArgsBuilder.libraryAllBookQuery(collectionLibraryUid, MetadataTestUtils.defaultContentTypes(), MetadataQueryArgsBuilder.getOrderByName().descending());
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
        list = MetaDataUtils.verifyReadedStatus(list, BookFilter.READ);
        assertTrue(list.size() == 1);

        //test finish read with libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryFinishReadQuery(collectionLibraryUid, SortBy.RecentlyRead, SortOrder.Desc);
        list = providerBase.findMetadata(getContext(), args);
        list = MetaDataUtils.verifyReadedStatus(list, BookFilter.READ);
        assertTrue(list.size() == 0);

        // test search with libraryUniqueId
        args = MetadataQueryArgsBuilder.librarySearchQuery(collectionLibraryUid, testSearch, SortBy.Name, SortOrder.Desc);
        args.query = testSearch;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 2);
        assertEquals(list.get(0).getName(), list.get(1).getName());

        args = MetadataQueryArgsBuilder.libraryTagsFilterQuery(collectionLibraryUid, MetadataTestUtils.getFormatTagSet(), SortBy.Name, SortOrder.Desc);
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

        LinkedHashMap<String, Metadata> metadataList = MetadataTestUtils.getRandomMetadata(metadataCount);
        LinkedHashMap<String, Library> libraryList = getRandomLibrary(libraryCount);

        int i = 0;
        for (Map.Entry<String, Metadata> entry : metadataList.entrySet()) {
            String md5 = entry.getValue().getIdString();
            String libraryId = MetadataTestUtils.getLibraryByIndex(libraryList, i % libraryCount).getIdString();
            MetadataCollection collection = new MetadataCollection(libraryId, md5);
            providerBase.addMetadataCollection(getContext(), collection);
            ++i;
        }
        for (i = 0; i < libraryList.size(); i++) {
            List<MetadataCollection> list = providerBase.loadMetadataCollection(getContext(), MetadataTestUtils.getLibraryByIndex(libraryList, i).getIdString());
            assertTrue(list.size() >= 1);
            if (i == 0) {
                assertTrue(list.size() >= 2);
            }
        }

        for (i = 0; i < libraryList.size(); i++) {
            providerBase.deleteMetadataCollection(getContext(),
                    MetadataTestUtils.getLibraryByIndex(libraryList, i).getIdString(),
                    MetadataTestUtils.getMetadataByIndex(metadataList, i).getIdString());
            int size = providerBase.loadMetadataCollection(getContext(),
                    MetadataTestUtils.getLibraryByIndex(libraryList, i).getIdString()).size();
            assertTrue(size == 0);
        }
    }


}

