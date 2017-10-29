package com.onyx.android.sdk;

import android.app.Application;
import android.os.Environment;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.QueryArgs;
import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.data.model.Metadata;
import com.onyx.android.sdk.data.model.MetadataCollection;
import com.onyx.android.sdk.data.model.ReadingProgress;
import com.onyx.android.sdk.data.provider.DataProviderBase;
import com.onyx.android.sdk.data.provider.DataProviderManager;
import com.onyx.android.sdk.data.rxrequest.data.db.RxMetadataRequest;
import com.onyx.android.sdk.data.utils.QueryBuilder;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import io.reactivex.functions.Consumer;

import static com.onyx.android.sdk.utils.TestUtils.generateRandomFile;
import static com.onyx.android.sdk.utils.TestUtils.randomStringList;

/**
 * Created by john on 29/10/2017.
 * adb shell pm grant com.onyx.android.sdk.dataprovider.test android.permission.WRITE_EXTERNAL_STORAGE
 * adb shell pm grant com.onyx.android.sdk.dataprovider.test android.permission.READ_EXTERNAL_STORAGE
 */

public class RxMetadataTest  extends ApplicationTestCase<Application> {

    private static boolean dbInit = false;

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

}
