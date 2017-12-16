package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.touch.RawInputReader;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.TestUtils;

/**
 * Created by john on 29/9/2017.
 */

public class RawInputReaderTest extends ApplicationTestCase<Application> {

    public RawInputReaderTest() {
        super(Application.class);
    }

    public void testShapeDocumentOpen() {
        RawInputReader rawInputReader = new RawInputReader();
        for(int i = 0; i < 10000; ++i) {
            rawInputReader.start();
            int sleep = TestUtils.randInt(1, 10);
            TestUtils.sleep(1000 * sleep);
            rawInputReader.quit();
            Log.e("###", "Testing round: " + i + " finished.");
        }
    }

    public void testBinderLoad() {
        float src[] = new float[] { 1000, 1000 };
        float dst[] = new float[2];

        for (int i = 0; i < 200000; i++) {
            EpdController.mapToView(null, src, dst);
            if (dst[0] <= 0 || dst[1] <= 0) {
                Debug.e(getClass(), "current loop: " + i);
                assertTrue(false);
            }
        }
    }
}
