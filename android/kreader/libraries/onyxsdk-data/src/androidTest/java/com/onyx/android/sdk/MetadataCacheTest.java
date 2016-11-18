package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.cache.DataCacheManager;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.request.data.MetadataRequest;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.onyx.android.sdk.MetadataTestUtils.awaitCountDownLatch;
import static com.onyx.android.sdk.MetadataTestUtils.getAscString;

/**
 * Created by zhuzeng on 16/11/2016.
 */

public class MetadataCacheTest extends ApplicationTestCase<Application> {


    public MetadataCacheTest() {
        super(Application.class);
    }

    public void testLibraryMetadataCache() {
        MetadataTestUtils.init(getContext());
        DataManager dataManager = new DataManager();
        DataCacheManager cacheManager = dataManager.getDataManagerHelper().getDataCacheManager();
        DataProviderBase providerBase = dataManager.getDataManagerHelper().getDataProvider();
        providerBase.clearMetadata();
        providerBase.clearLibrary();
        providerBase.clearMetadataCollection();

        int count = TestUtils.randInt(10, 20);
        String[] ascString = getAscString(count);
        String libraryUniqueId = MetadataTestUtils.generateRandomUUID();
        LinkedHashMap<String, Metadata> metadataList = MetadataTestUtils.getRandomMetadata(count);
        for (int i = 0; i < count; i++) {
            final Metadata metadata = MetadataTestUtils.getMetadataByIndex(metadataList, i);
            metadata.setName(ascString[i]);
            if (i == 0) {
                metadata.setProgress("12/12");
                metadata.setLastAccess(new Date());
            }

            if (i == 1) {
                metadata.setProgress("12/33");
                metadata.setLastAccess(new Date());
            }

            if (i > 1 && i < 5) { //3
                MetadataCollection collection = new MetadataCollection();
                collection.setLibraryUniqueId(libraryUniqueId);
                collection.setDocumentUniqueId(metadata.getIdString());
                collection.save();
                if (i == 2) {
                    metadata.setLastAccess(new Date());
                }
                if (i == 4) {
                    metadata.setTags(MetadataTestUtils.getRandomFormatTag());
                }
            }

            metadata.save();
        }

        //test all and desc by Name
        QueryArgs args = MetadataQueryArgsBuilder.libraryAllBookQuery(null, SortBy.Name, SortOrder.Desc);
        List<Metadata> list = dataManager.getDataManagerHelper().getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() >= metadataList.size() - 3);
        Metadata tmp = list.get(0);
        for (int i = list.size() - 1, j = 0; i >= 0; i--, j++) {
            Metadata metadata = list.get(j);
            int result = MetaDataUtils.compareStringAsc(tmp.getName(), metadata.getName());
            assertTrue(result >= 0);
            tmp = metadata;
        }

        // test cache
        LibraryCache libraryCache = cacheManager.getLibraryCache(null);
        assertEquals(libraryCache.getValueList().size(), list.size());
        for (Metadata metadata : list) {
            assertTrue(libraryCache.getValueList().contains(metadata.getIdString()));
            assertEquals(cacheManager.getMetadataById(metadata.getIdString()), metadata);
        }

        //test reading and desc by Name
        args = MetadataQueryArgsBuilder.libraryRecentReadingQuery(null, SortBy.Name, SortOrder.Desc);
        list = dataManager.getDataManagerHelper().getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 2);

        //test finish read and desc by Name
        args = MetadataQueryArgsBuilder.libraryFinishReadQuery(null, SortBy.Name, SortOrder.Desc);
        list = dataManager.getDataManagerHelper().getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 1);

        //test all has libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryAllBookQuery(libraryUniqueId, SortBy.Name, SortOrder.Desc);
        list = dataManager.getDataManagerHelper().getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 3);
        assertTrue(cacheManager.getLibraryCache(libraryUniqueId).getValueList().size() == 3);

        //test newBook has libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryNewBookListQuery(libraryUniqueId, SortBy.Name, SortOrder.Desc);
        list = dataManager.getDataManagerHelper().getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 2);

        //test tag has libraryUniqueId
        args = MetadataQueryArgsBuilder.libraryTagsFilterQuery(libraryUniqueId, MetadataTestUtils.getFormatTagSet(), SortBy.Name, SortOrder.Desc);
        list = dataManager.getDataManagerHelper().getLibraryMetadataList(getContext(), args);
        assertNotNull(list);
        assertTrue(list.size() == 1);
        List<Metadata> metadatas = cacheManager.getLibraryCache(libraryUniqueId).getValueList();
        assertTrue(metadatas.contains(list.get(0).getIdString()));
        assertNotNull(cacheManager.getMetadataById(metadatas.get(0).getIdString()));
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


}
