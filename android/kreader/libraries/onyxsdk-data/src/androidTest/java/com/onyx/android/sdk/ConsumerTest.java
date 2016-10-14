package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.data.CloudManager;
import com.onyx.android.sdk.data.model.Consumer;
import com.onyx.android.sdk.data.model.OnyxAccount;
import com.onyx.android.sdk.data.v1.OnyxConsumerService;
import com.onyx.android.sdk.data.v1.ServiceFactory;

import java.util.List;

import retrofit2.Response;

/**
 * Created by suicheng on 2016/9/23.
 */
public class ConsumerTest extends ApplicationTestCase<Application> {

    static OnyxConsumerService service;
    static OnyxAccount currentAccount;

    public ConsumerTest() {
        super(Application.class);
    }

    private final OnyxConsumerService getService() {
        if (service == null) {
            CloudManager cloudManager = new CloudManager();
            service = ServiceFactory.getConsumerService(cloudManager.getCloudConf().getApiBase());
        }
        return service;
    }

    public static OnyxAccount getCurrentAccount() throws Exception {
        if (currentAccount == null || currentAccount.sessionToken == null) {
            currentAccount = AccountTest.testSignUpRequest();
        }
        return currentAccount;
    }

    public void testConsumerList() throws Exception {
        Response<List<Consumer>> response = getService().getConsumerList(getCurrentAccount().sessionToken).execute();
        assertNotNull(response);
        assertNotNull(response.body());
        List<Consumer> list = response.body();
        assertTrue(list.size() >= 1);

        //test one consumer
        Response<Consumer> consumerResponse = getService().getConsumer(list.get(0).id, getCurrentAccount().sessionToken).execute();
        assertNotNull(consumerResponse);
        assertNotNull(consumerResponse.body());
        Consumer consumer = consumerResponse.body();
        assertEquals(list.get(0).name, consumer.name);
    }

}
