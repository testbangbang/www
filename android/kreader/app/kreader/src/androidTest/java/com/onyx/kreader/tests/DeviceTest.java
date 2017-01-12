package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.reader.utils.ImageUtils;

/**
 * Created by zhuzeng on 29/12/2016.
 */

public class DeviceTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    static private String TAG = CFATest.class.getSimpleName();


    public DeviceTest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void testSystemIntegrity() throws Exception {
        for(int i = 0; i < 5000; ++i) {
            boolean legal = Device.currentDevice().isLegalSystem(getActivity());
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
