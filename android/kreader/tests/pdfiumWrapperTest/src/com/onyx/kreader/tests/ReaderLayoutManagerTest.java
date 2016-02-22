package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.host.layout.LayoutManagerCallback;
import com.onyx.kreader.host.layout.LayoutProvider;
import com.onyx.kreader.host.layout.ReaderLayoutManager;
import com.onyx.kreader.host.math.PageManager;
import com.onyx.kreader.host.options.ReaderConstants;
import com.onyx.kreader.host.wrapper.Reader;

/**
 * Created by zengzhu on 2/22/16.
 */
public class ReaderLayoutManagerTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderLayoutManagerTest() {
        super(ReaderTestActivity.class);
    }

    public void testLayoutManager() throws Exception  {
        FakeReader reader = new FakeReader();
        reader.open();
        ReaderLayoutManager layoutManager = new ReaderLayoutManager(reader,
                reader,
                reader,
                reader);

        layoutManager.init();
        assertTrue(layoutManager.setCurrentLayout(ReaderConstants.SINGLE_PAGE));
        assertTrue(layoutManager.getCurrentLayoutType().equalsIgnoreCase(ReaderConstants.SINGLE_PAGE));
    }


}
