package com.onyx.android.sdk;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.DataCacheManager;
import com.onyx.android.sdk.data.BookFilter;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.compatability.OnyxThumbnail;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.data.model.Library_Table;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.model.Thumbnail;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.QueryCriteria;
import com.onyx.android.sdk.data.model.ReadingProgress;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.MetadataRequest;
import com.onyx.android.sdk.data.utils.MetaDataUtils;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.OrderBy;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.File;
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

        QueryArgs args = MetadataQueryArgsBuilder.bookListQuery(defaultContentTypes(), MetadataQueryArgsBuilder.getOrderByName());
        List<Metadata> testList = dataProvider.findMetadata(getContext(), args);
        assertNotNull(testList);
        assertTrue(testList.size() > 0);

        final QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.author.addAll(authors);
        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryCriteria dummyQueryCriteria = new QueryCriteria();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == 0);
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

        final QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.title.addAll(title);
        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryCriteria dummyQueryCriteria = new QueryCriteria();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == 0);
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

        final QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.series.addAll(series);
        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryCriteria dummyQueryCriteria = new QueryCriteria();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == 0);
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

        final QueryCriteria queryCriteria = new QueryCriteria();
        queryCriteria.tags.addAll(tags);
        final List<Metadata> result = dataProvider.findMetadata(getContext(), queryCriteria);
        assertNotNull(result);
        assertTrue(result.size() == 1);

        final Metadata target = result.get(0);
        assertEquals(target.getIdString(), origin.getIdString());

        final QueryCriteria dummyQueryCriteria = new QueryCriteria();
        final List<Metadata> list = dataProvider.findMetadata(getContext(), dummyQueryCriteria);
        assertNotNull(list);
        assertTrue(list.size() == 0);
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

        QueryArgs queryArgs = MetadataQueryArgsBuilder.tagsFilterQuery(tagSet,
                OrderBy.fromProperty(Metadata_Table.tags).descending());
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

        QueryArgs queryArgs = MetadataQueryArgsBuilder.bookListQuery(defaultContentTypes(),
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
     * Closely related to {@link MetadataTest#testDataCacheQueryArgs}.
     */
    public DataCacheManager testDataCacheSave() {
        DataProviderBase providerBase = getProviderBase();
        providerBase.clearMetadata();

        DataCacheManager cacheManager = new DataCacheManager();
        int parentCount = 15;//not beyond 25
        int min = 9;//must be large than and equal 9
        String[] title = getAscString(parentCount);
        String[] tags = getFormatTags();
        String uuid[] = new String[parentCount];
        int[] count = new int[parentCount];
        Random random = new Random();
        for (int i = 0; i < parentCount; i++) {
            count[i] = random.nextInt(parentCount);
            if (count[i] < min) {
                count[i] = min;
            }
            String parentId = uuid[i] = generateRandomUUID();
            List<Metadata> list = new ArrayList<>();
            for (int j = 0; j < count[i]; j++) {
                Metadata data = getRandomMetadata();
                data.setLastAccess(new Date(System.currentTimeMillis() + j * 1000));
                data.setPublisher(generateRandomUUID().substring(0, 6));
                data.setTitle(title[j]);// The following operation are not allowed to edit title
                MetadataCollection collection = new MetadataCollection();

                collection.setDocumentUniqueId(data.getIdString());
                if (j == 1) {
                    collection.setDocumentUniqueId("AAAAA");
                }
                collection.save();

                if (j == 0) {
                    data.setProgress("12/33");//for next test progress
                }
                if (j == 1) {
                    data.setProgress("12/12");//for next test progress
                }
                if (j == 2) {
                    data.setName("will be good"); //for next test search
                }
                if (j == 3) {
                    data.setName("I will be");//for next test
                }
                if (j == 4) {
                    data.setAuthors("will have a holiday");//for next test
                }
                if (j >= 5 && j < 8) { //for next test tag  5,6,7
                    data.setTags(tags[random.nextInt(tags.length)]);
                }
                if (j == 8) {
                    data.setTags("error"); //will be ignore, the result size must be 3
                }
                data.save();
                list.add(data);
            }
            cacheManager.addAll(parentId, list);
        }

        assertTrue(cacheManager.getLibraryMapHashMap().size() == parentCount);
        for (int i = 0; i < parentCount; i++) {
            String s = uuid[i];
            List<Metadata> list = cacheManager.getMetadataList(s);
            assertTrue(list.size() == count[i]);
        }
        return cacheManager;
    }

    public void testDataCacheQueryArgs() {
        Random random = new Random();
        DataCacheManager cacheManager = testDataCacheSave();
        HashMap<String, List<Metadata>> hashMap = cacheManager.getLibraryMapHashMap();
        String[] keys = hashMap.keySet().toArray(new String[]{});

        int index = random.nextInt(keys.length);
        QueryArgs args = new QueryArgs();
        args.order = SortOrder.Desc;
        args.sortBy = SortBy.BookTitle;
        args.parentId = keys[index];

        //test sortBy title desc
        List<Metadata> list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == hashMap.get(keys[index]).size());
        String[] ascString = getAscString(list.size());
        int j = 0;
        for (int i = ascString.length - 1; i >= 0; i--, j++) {
            assertTrue(list.get(j).getTitle().equals(ascString[i]));
        }

        //test sortBy size desc
        args.sortBy = SortBy.Size;
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() > 0);
        Metadata tmp = list.get(0);
        for (Metadata metadata : list) {
            assertTrue(tmp.getSize() >= metadata.getSize());
            tmp = metadata;
        }

        //test sortBy createdAt desc
        args.sortBy = SortBy.CreationTime;
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() > 0);
        tmp = list.get(0);
        for (Metadata metadata : list) {
            assertTrue(tmp.getCreatedAt().getTime() >= metadata.getCreatedAt().getTime());
            tmp = metadata;
        }

        //test sortBy size desc
        args.sortBy = SortBy.LastOpenTime;
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() > 0);
        tmp = list.get(0);
        for (Metadata metadata : list) {
            assertTrue(tmp.getLastAccess().getTime() >= metadata.getLastAccess().getTime());
            tmp = metadata;
        }

        //test sortBy publisher desc
        args.sortBy = SortBy.Publisher;
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() > 0);
        tmp = list.get(0);
        for (Metadata metadata : list) {
            int result = MetaDataUtils.compareStringAsc(tmp.getPublisher().substring(0, 1),
                    metadata.getPublisher().substring(0, 1));
            assertTrue(result >= 0);
            tmp = metadata;
        }

        //test filter new books
        args.filter = BookFilter.NEW_BOOKS;
        list = cacheManager.getMetadataList(args);
        for (Metadata metadata : list) {
            assertTrue(StringUtils.isNullOrEmpty(metadata.getProgress()));
        }

        //test filter reading
        args.filter = BookFilter.READING;
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == 2);

        //test filter readed
        args.filter = BookFilter.READED;
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == 1);

        //test filter search
        args.filter = BookFilter.SEARCH;
        args.query = "will";
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == 3);

        //test filter tags
        args.filter = BookFilter.TAG;
        args.tags = getFormatTagSet();
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == 3);

        //test all and contentType
        Set<String> set = new HashSet<>();
        set.add("apk");//make sure that it is not contained in defaultContentTypes
        args.contentType = set;
        args.filter = BookFilter.values()[random.nextInt(BookFilter.values().length)];
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == 0);

        args.limit = 2;
        args.contentType = null;
        args.filter = BookFilter.ALL;
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == args.limit);

        //test offset
        args.limit = Integer.MAX_VALUE;
        args.offset = 1;
        args.filter = BookFilter.READED;//(based on above,only 1 count :READED,:READING)
        list = cacheManager.getMetadataList(args);
        assertTrue(list.size() == 0);

        args.offset = 3;
        args.filter = BookFilter.ALL;
        list = cacheManager.getMetadataList(args);
        int size = hashMap.get(keys[index]).size();
        assertTrue(list.size() == size - args.offset);
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
        String collectionLibraryUid = generateRandomUUID();
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
                collection.setLibraryUniqueId(collectionLibraryUid);//one can't do without the other
            }
            if (i == 6) {
                collection.setLibraryUniqueId(collectionLibraryUid);
                metadata.setLastAccess(new Date());
                metadata.setProgress("12/15");
            }
            collection.setIdString(generateRandomUUID());
            collection.setDocumentUniqueId(metadata.getIdString());
            collection.save();
            metadata.save();
        }

        // test sortBy name desc and load
        QueryArgs args = MetadataQueryArgsBuilder.bookListQuery(defaultContentTypes(), MetadataQueryArgsBuilder.getOrderByName().descending());
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
        args.parentId = collectionLibraryUid;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 3);

        // test new book
        args = MetadataQueryArgsBuilder.newBookListQuery();
        args.parentId = collectionLibraryUid;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 2);

        //test recent reading and OrderBy updatedAt desc
        args = MetadataQueryArgsBuilder.recentReadingQuery();
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 3);
        tmp = list.get(0);
        for (int i = 0; i < list.size(); i++) {
            assertTrue(tmp.getUpdatedAt().getTime() >= list.get(i).getUpdatedAt().getTime());
            tmp = list.get(i);
        }

        //test recent reading and has parentId
        args.parentId = collectionLibraryUid;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 1);

        //test recentAdd
        args = MetadataQueryArgsBuilder.recentAddQuery();
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 4);

        //test recentAdd and has parentId
        args.parentId = collectionLibraryUid;
        list = providerBase.findMetadata(getContext(), args);
        assertTrue(list.size() == 2);
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
}

