package com.onyx.reader.tests;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.reader.api.ReaderPlugin;
import com.onyx.reader.common.BaseCallback;
import com.onyx.reader.common.BaseRequest;
import com.onyx.reader.host.layout.ReaderLayoutManager;
import com.onyx.reader.host.request.OpenRequest;
import com.onyx.reader.host.wrapper.Reader;
import com.onyx.reader.test.ReaderTestActivity;

/**
 * Created by zhuzeng on 10/5/15.
 */
public class ReaderLayoutManagerTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderLayoutManagerTest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void testLayout() {

    }
}
