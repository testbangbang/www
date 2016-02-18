package com.onyx.kreader.tests;

import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderPlugin;
import com.onyx.kreader.plugins.images.ImagesReaderPlugin;

/**
 * Created by zhuzeng on 2/18/16.
 */
public class ReaderPluginImagesTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderPluginImagesTest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void testImagePlugin1() throws Exception {
        ReaderPlugin plugin = new ImagesReaderPlugin(getActivity(), null);
        ReaderDocument document = plugin.open("/mnt/sdcard/Books/a.png", null, null);
        assertNotNull(document);
        RectF rect = document.getPageOriginSize(String.valueOf(0));
        assertTrue(rect.width() > 0);
        assertTrue(rect.height() > 0);
        document.close();
    }

}
