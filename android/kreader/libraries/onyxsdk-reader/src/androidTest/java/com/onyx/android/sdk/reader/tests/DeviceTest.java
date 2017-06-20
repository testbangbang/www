package com.onyx.android.sdk.reader.tests;

import android.app.Application;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.reader.utils.ImageUtils;

/**
 * Created by zhuzeng on 29/12/2016.
 */

public class DeviceTest extends ApplicationTestCase<Application> {

    static private String TAG = CFATest.class.getSimpleName();


    public DeviceTest() {
        super(Application.class);
    }

    public void testSystemIntegrity() throws Exception {
        for(int i = 0; i < 5000; ++i) {
            boolean legal = Device.currentDevice().isLegalSystem(getContext());
            assertTrue(legal);
        }
    }

    public void testSystemIntegrityJni() throws Exception {
        for(int i = 0; i < 5000; ++i) {
            long start = System.currentTimeMillis();
            boolean legal = ImageUtils.isValidPage();
            long end = System.currentTimeMillis();
            assertTrue(end - start < 500);
            assertTrue(legal);
        }
    }

}
