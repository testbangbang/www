package com.onyx.android.sdk;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.Metadata_Table;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.provider.LocalDataProvider;
import com.onyx.android.sdk.data.QueryCriteria;
import com.onyx.android.sdk.data.model.ReadingProgress;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.request.data.MetaDataRequest;
import com.onyx.android.sdk.data.utils.MetadataQueryArgsBuilder;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.OrderBy;

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

        List<String> authors = randomStringList();
        Metadata origin = randomMetadata(testFolder(), true);
        origin.setAuthors(StringUtils.join(authors, Metadata.DELIMITER));
        dataProvider.saveMetadata(getContext(), origin);

        int limit = TestUtils.randInt(10, 100);
        for (int i = 0; i < limit; ++i) {
            Metadata metadata = randomMetadata(testFolder(), true);
            dataProvider.saveMetadata(getContext(), metadata);
        }

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
        final MetaDataRequest metaDataRequest = new MetaDataRequest(queryArgs);
        dataManager.submit(getContext(), metaDataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(metaDataRequest.getList());
                assertTrue(metaDataRequest.getList().size() >= count / divider);

                boolean result = false;
                for (Metadata metadata : metaDataRequest.getList()) {
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
        final MetaDataRequest metaDataRequest = new MetaDataRequest(queryArgs);
        dataManager.submit(getContext(), metaDataRequest, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                assertNull(e);
                assertNotNull(metaDataRequest.getList());
                assertTrue(metaDataRequest.getList().size() > 0);
                assertTrue(metaDataRequest.getList().size() == count);
                boolean result = false;
                for (Metadata metadata : metaDataRequest.getList()) {
                    for (Metadata test : testMetadata) {
                        result |= metadata.getIdString().equals(test.getIdString());
                    }
                    assertTrue(result);
                    result = false;
                }

                List<Metadata> list = metaDataRequest.getList();
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
}

