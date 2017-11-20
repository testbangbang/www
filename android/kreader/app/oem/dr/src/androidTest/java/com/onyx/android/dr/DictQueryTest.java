package com.onyx.android.dr;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;

import com.onyx.android.dr.common.Constants;
import com.onyx.android.sdk.dict.DictDatasInit;
import com.onyx.android.sdk.dict.data.DictionaryManager;
import com.onyx.android.sdk.dict.data.DictionaryQueryResult;
import com.onyx.android.sdk.dict.data.bean.DictionaryInfo;
import com.onyx.android.sdk.dict.request.QueryWordRequest;
import com.onyx.android.sdk.dict.request.common.DictBaseCallback;
import com.onyx.android.sdk.dict.request.common.DictBaseRequest;
import com.onyx.android.sdk.dict.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Created by zhouzhiming on 17-6-26.
 */
public class DictQueryTest extends ApplicationTestCase<DRApplication> {
    private StringBuffer buffer = new StringBuffer();
    private QueryWordRequest queryWordRequest;
    private static final String TAG = DictQueryTest.class.getSimpleName();
    private List<String> stringList;
    private final String word = "book";

    public DictQueryTest() {
        super(DRApplication.class);
    }

    @NonNull
    private DictionaryManager addDictPaths() {
        List<String> dictPaths = new ArrayList<>();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            dictPaths.add(path + Constants.ENGLISH_DICTIONARY);
            dictPaths.add(path + Constants.CHINESE_DICTIONARY);
            dictPaths.add(path + Constants.JAPANESE_DICTIONARY);
            dictPaths.add(path + Constants.FRENCH_DICTIONARY);
        }
        DictDatasInit dictDatasInit = new DictDatasInit(DRApplication.getInstance().getApplicationContext(), dictPaths);
        DictionaryManager dictionaryManager = dictDatasInit.getDictionaryManager();
        return dictionaryManager;
    }

    public void testWordDictQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DictionaryManager dictionaryManager = addDictPaths();
        queryWordRequest = new QueryWordRequest(word);
        stringList = Utils.loadLocalDict(Constants.ENGLISH_DICTIONARY);
        dictionaryManager.sendRequest(DRApplication.getInstance().getApplicationContext(), queryWordRequest, stringList, new DictBaseCallback() {
            @Override
            public void done(DictBaseRequest request, Exception e) {
                assertNull(e);
                Map<String, DictionaryQueryResult> map = queryWordRequest.queryResult;
                assertQueryResult(map, word);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testFuzzyDictQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DictionaryManager dictionaryManager = addDictPaths();
        queryWordRequest = new QueryWordRequest(word);
        stringList = Utils.loadLocalDict(Constants.ENGLISH_DICTIONARY);
        dictionaryManager.sendRequest(DRApplication.getInstance().getApplicationContext(), queryWordRequest, stringList, new DictBaseCallback() {
            @Override
            public void done(DictBaseRequest request, Exception e) {
                assertNull(e);
                Map<String, DictionaryQueryResult> map = queryWordRequest.queryResult;
                assertQueryResult(map, word);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testPhraseDictQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DictionaryManager dictionaryManager = addDictPaths();
        queryWordRequest = new QueryWordRequest(word);
        stringList = Utils.loadLocalDict(Constants.ENGLISH_DICTIONARY);
        dictionaryManager.sendRequest(DRApplication.getInstance().getApplicationContext(), queryWordRequest, stringList, new DictBaseCallback() {
            @Override
            public void done(DictBaseRequest request, Exception e) {
                assertNull(e);
                Map<String, DictionaryQueryResult> map = queryWordRequest.queryResult;
                assertQueryResult(map, word);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    public void testExampleDictQuery() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        DictionaryManager dictionaryManager = addDictPaths();
        queryWordRequest = new QueryWordRequest(word);
        stringList = Utils.loadLocalDict(Constants.ENGLISH_DICTIONARY);
        dictionaryManager.sendRequest(DRApplication.getInstance().getApplicationContext(), queryWordRequest, stringList, new DictBaseCallback() {
            @Override
            public void done(DictBaseRequest request, Exception e) {
                assertNull(e);
                Map<String, DictionaryQueryResult> map = queryWordRequest.queryResult;
                assertQueryResult(map, word);
                countDownLatch.countDown();
            }
        });
        countDownLatch.await();
    }

    private void assertQueryResult(Map<String, DictionaryQueryResult> map, String word) {
        assertNotNull(map);
        assertTrue(map.size() > 0);
        for (Map.Entry<String, DictionaryQueryResult> entry : map.entrySet()) {
            DictionaryQueryResult queryResult = entry.getValue();
            String originWord = queryResult.getOriginWord();
            String candidate = queryResult.candidate;
            String explanation = queryResult.explanation;
            DictionaryInfo dictionary = queryResult.dictionary;
            int entryIndex = queryResult.entryIndex;
            String soundPath = queryResult.soundPath;
            assertEquals(word, originWord);
            assertNotNull(candidate);
            assertNotNull(dictionary);
            assertNotNull(entryIndex);
            assertNotNull(soundPath);
            assertNotNull(explanation);
        }
    }
}