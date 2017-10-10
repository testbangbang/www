package com.onyx.android.sdk.scribble;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.onyx.android.sdk.scribble.data.NoteDocument;
import com.onyx.android.sdk.scribble.data.NotePage;
import com.onyx.android.sdk.scribble.touch.RawInputReader;
import com.onyx.android.sdk.utils.TestUtils;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.config.ShapeGeneratedDatabaseHolder;

import java.util.UUID;

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
}
