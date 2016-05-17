package com.onyx.android.sdk.device;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.api.device.epd.EpdController;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testEpdController() {
        EpdController.applyApplicationFastMode("a-test", true, true);
        EpdController.applyApplicationFastMode("a-test", false, true);
    }
}