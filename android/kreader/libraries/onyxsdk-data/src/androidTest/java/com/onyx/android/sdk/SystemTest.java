package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.db.table.OnyxSystemConfigProvider;
import com.onyx.android.sdk.data.provider.SystemConfigProvider;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowManager;

import java.util.UUID;

/**
 * Created by suicheng on 2017/6/22.
 */

public class SystemTest extends ApplicationTestCase<Application> {

    public SystemTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    private void init() {
        DataManager.init(getContext(), null);
        FlowManager.getContext().getContentResolver().delete(OnyxSystemConfigProvider.CONTENT_URI, null, null);
    }

    public void testKeyValueItem() {
        init();
        int round = TestUtils.randInt(30, 50);
        int countPerRound = TestUtils.randInt(12, 20);
        for (int i = 0; i < round; i++) {
            for (int j = 0; j < countPerRound; j++) {
                testStringItem();
                testIntItem();
                testBooleanItem();
            }
        }
    }

    private void testStringItem() {
        String key = UUID.randomUUID().toString();
        String value = UUID.randomUUID().toString();
        boolean success = SystemConfigProvider.setStringValue(getContext(), key, value);
        assertTrue(success);
        String resultValue = SystemConfigProvider.getStringValue(getContext(), key);
        assertNotNull(resultValue);
        assertEquals(value, resultValue);
    }

    private void testIntItem() {
        String key = UUID.randomUUID().toString();
        int value = TestUtils.randInt(1, Integer.MAX_VALUE);
        boolean success = SystemConfigProvider.setIntValue(getContext(), key, value);
        assertTrue(success);
        int resultValue = SystemConfigProvider.getIntValue(getContext(), key);
        assertEquals(value, resultValue);
    }

    private void testBooleanItem() {
        String key = UUID.randomUUID().toString();
        boolean value = TestUtils.randInt(0, 10) % 2 == 0;
        boolean success = SystemConfigProvider.setBooleanValue(getContext(), key, value);
        assertTrue(success);
        boolean resultValue = SystemConfigProvider.getBooleanValue(getContext(), key);
        assertEquals(value, resultValue);
    }
}
