package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.cache.DataCacheManager;
import com.onyx.android.sdk.data.cache.LibraryCache;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

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
        assertEquals(libraryCache.getIdList().size(), list.size());
        for (Metadata metadata : list) {
            assertTrue(libraryCache.getIdList().contains(metadata.getIdString()));
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
        assertTrue(cacheManager.getLibraryCache(libraryUniqueId).getIdList().size() == 3);

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
        List<String> md5List = cacheManager.getLibraryCache(libraryUniqueId).getIdList();
        assertTrue(md5List.contains(list.get(0).getIdString()));
        assertNotNull(cacheManager.getMetadataById(md5List.get(0)));
    }

}
