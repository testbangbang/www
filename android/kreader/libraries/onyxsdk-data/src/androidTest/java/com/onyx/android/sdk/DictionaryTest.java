package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.model.Dictionary;
import com.onyx.android.sdk.data.model.Dictionary_Table;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.model.ProductResult;
import com.onyx.android.sdk.data.utils.StoreUtils;
import com.onyx.android.sdk.data.v1.OnyxDictionaryService;
import com.onyx.android.sdk.data.v1.ServiceFactory;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by suicheng on 2016/10/10.
 */

public class DictionaryTest extends ApplicationTestCase<Application> {

    static boolean dbInit = false;
    static OnyxDictionaryService service;

    public DictionaryTest() {
        super(Application.class);
    }

    private final OnyxDictionaryService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getDictionaryService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public void init() {
        if (dbInit) {
            return;
        }
        dbInit = true;
        DataManager.init(getContext(), null);
    }

    private static List<String> generateRandomStringList() {
        List<String> list = new ArrayList<>();
        int count = TestUtils.randInt(5, 10);
        for (int i = 0; i < count; i++) {
            list.add(TestUtils.randString());
        }
        return list;
    }

    private static Set<String> generateRandomStringSet() {
        Set<String> set = new HashSet<>();
        int count = TestUtils.randInt(5, 10);
        for (int i = 0; i < count; i++) {
            set.add(TestUtils.randString());
        }
        return set;
    }

    private static String generateRandomUUID() {
        return UUID.randomUUID().toString();
    }

    private static Dictionary getRandomDictionary() {
        Dictionary dictionary = new Dictionary();
        dictionary.sourceLanguage = generateRandomUUID();
        dictionary.targetLanguage = generateRandomUUID();
        dictionary.title = TestUtils.randomEmail();
        dictionary.officialComment = TestUtils.randString();
        dictionary.category = generateRandomStringList();
        return dictionary;
    }

    private static List<Dictionary> getRandomDictionaryList(int count) {
        List<Dictionary> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(getRandomDictionary());
        }
        return list;
    }

    private static Dictionary queryItem(String sourceLanguage) {
        return new Select().from(Dictionary.class)
                .where(Dictionary_Table.sourceLanguage.eq(sourceLanguage))
                .querySingle();
    }

    public void testDictionaryStorage() throws Exception {
        init();
        StoreUtils.clearTable(Dictionary.class);

        int count = TestUtils.randInt(10, 15);
        List<Dictionary> list = getRandomDictionaryList(count);

        //test save list
        StoreUtils.saveToLocalFast(list, Dictionary.class);

        //test query list
        List<Dictionary> testList = StoreUtils.queryDataList(Dictionary.class);
        assertNotNull(testList);
        assertTrue(testList.size() == list.size());
        for (int i = 0; i < list.size(); i++) {
            testList.get(i).afterLoad();
            assertEquals(list.get(i).title, testList.get(i).title);
            assertEquals(list.get(i).sourceLanguage, testList.get(i).sourceLanguage);
            assertTrue(list.get(i).category.containsAll(testList.get(i).category));
        }
        int limit = TestUtils.randInt(1, list.size());
        testList = StoreUtils.queryDataList(Dictionary.class, limit);
        assertNotNull(testList);
        assertTrue(testList.size() == limit);

        //test add one item
        Dictionary newDictionary = getRandomDictionary();
        newDictionary.insert();
        assertTrue(StoreUtils.queryDataList(Dictionary.class).size() == list.size() + 1);

        //test update one item
        newDictionary.description = TestUtils.randString();
        newDictionary.update();
        assertEquals(queryItem(newDictionary.sourceLanguage).description, newDictionary.description);

        //test query one item
        Dictionary testDictionary = queryItem(newDictionary.sourceLanguage);
        assertNotNull(testDictionary);
        assertNotNull(testDictionary.sourceLanguage);
        assertEquals(testDictionary.sourceLanguage, newDictionary.sourceLanguage);

        //test query random sourceLanguage
        assertNull(queryItem(generateRandomUUID()));

        //test delete one item
        newDictionary.delete();
        testDictionary = queryItem(newDictionary.sourceLanguage);
        assertNull(testDictionary);
        assertTrue(StoreUtils.queryDataList(Dictionary.class).size() == list.size());

        //test clear table
        StoreUtils.clearTable(Dictionary.class);
        assertTrue(StoreUtils.queryDataList(Dictionary.class).size() == 0);
    }

    public void testDictionaryNetwork() throws Exception {
        OnyxAccount account = AccountTest.testSignUpRequest();
        assertNotNull(account);
        String sessionToken = account.sessionToken;
        assertNotNull(sessionToken);

        //test all list
        Response<ProductResult<Dictionary>> response = getService().dictionaryList(null).execute();
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        if (response.body().count > 0) {
            //test get dictionary item
            int index = TestUtils.randInt(0, (int) response.body().count - 1);
            Dictionary dictionary = response.body().list.get(index);
            Response<Dictionary> dictionaryResponse = getService().dictionaryItem(dictionary.getGuid()).execute();
            assertTrue(dictionaryResponse.isSuccessful());
            assertNotNull(dictionaryResponse.body());
            Dictionary testDictionary = dictionaryResponse.body();
            assertNotNull(testDictionary.storage);
            assertTrue(testDictionary.storage.keySet().size() > 0);
            assertEquals(testDictionary.title, response.body().list.get(index).title);
            assertEquals(testDictionary.getGuid(), response.body().list.get(index).getGuid());

            if (!CollectionUtils.isNullOrEmpty(testDictionary.covers)) {
                //test get dictionary cover
                String typeKey = null;
                Iterator<String> iterator = testDictionary.covers.keySet().iterator();
                if (iterator.hasNext()) {
                    typeKey = iterator.next();
                }
                assertNotNull(typeKey);
                long size = testDictionary.covers.get(typeKey).get(Constant.DEFAULT_CLOUD_STORAGE).size;
                Response<ResponseBody> bytesResponse = getService().dictionaryCover(testDictionary.getGuid(), typeKey).execute();
                assertNotNull(bytesResponse);
                assertTrue(bytesResponse.isSuccessful());
                assertTrue(bytesResponse.body().bytes().length == size);
            }

            //test get dictionary data
            String formatKey = null;
            Iterator<String> iterator = testDictionary.storage.keySet().iterator();
            if (iterator.hasNext()) {
                formatKey = iterator.next();
            }
            assertNotNull(formatKey);
            long size = testDictionary.storage.get(formatKey).get(Constant.DEFAULT_CLOUD_STORAGE).size;
            Response<ResponseBody> bytesResponse = getService().dictionaryData(testDictionary.getGuid(), formatKey, sessionToken).execute();
            assertNotNull(bytesResponse);
            assertTrue(bytesResponse.isSuccessful());
            assertTrue(bytesResponse.body().bytes().length == size);
        }
    }

}
