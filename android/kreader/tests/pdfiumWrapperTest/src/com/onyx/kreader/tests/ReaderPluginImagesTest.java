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
        final String path = "/mnt/sdcard/Books/scaleToWidth.png";
        ReaderPlugin plugin = new ImagesReaderPlugin(getActivity(), null);
        ReaderDocument document = plugin.open(path, null, null);
        assertNotNull(document);
        final String position = document.getView(null).getNavigator().getPositionByPageName(path);
        RectF rect = document.getPageOriginSize(position);
        assertTrue(rect.width() > 0);
        assertTrue(rect.height() > 0);
        document.close();
    }

    public void testImagePlugin2() throws Exception {
        final String path = "/mnt/sdcard/Books/singlePage.png";
        ReaderPlugin plugin = new ImagesReaderPlugin(getActivity(), null);
        ReaderDocument document = plugin.open(path, null, null);
        assertNotNull(document);
        final String position = document.getView(null).getNavigator().getInitPosition();
        RectF rect = document.getPageOriginSize(position);
        assertTrue(rect.width() > 0);
        assertTrue(rect.height() > 0);
        document.close();
    }


}
