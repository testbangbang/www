package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import com.onyx.kreader.api.*;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.plugins.images.ImagesReaderPlugin;
import com.onyx.kreader.utils.BitmapUtils;
import com.onyx.kreader.utils.StringUtils;
import com.onyx.kreader.utils.TestUtils;

/**
 * Created by zhuzeng on 2/18/16.
 */
public class ReaderPluginImagesTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    public ReaderPluginImagesTest() {
        super("com.onyx.reader", ReaderTestActivity.class);
    }

    public void testImagePluginPngBase1() throws Exception {
        final String path = "/mnt/sdcard/Pictures/normal1.png";
        ReaderPlugin plugin = new ImagesReaderPlugin(getActivity(), null);
        ReaderDocument document = plugin.open(path, null, null);
        assertNotNull(document);
        final String position = document.getView(null).getNavigator().getPositionByPageName(path);
        RectF rect = document.getPageOriginSize(position);
        assertTrue(rect.width() > 0);
        assertTrue(rect.height() > 0);
        document.close();
    }

    public void testImagePluginPngBase2() throws Exception {
        final String path = "/mnt/sdcard/Pictures/normal2.png";
        ReaderPlugin plugin = new ImagesReaderPlugin(getActivity(), null);
        ReaderDocument document = plugin.open(path, null, null);
        assertNotNull(document);
        final ReaderNavigator navigator = document.getView(null).getNavigator();
        assertNotNull(navigator);
        assertTrue(navigator.getTotalPage() > 0);
        final String position = navigator.getInitPosition();
        assertTrue(StringUtils.isNotBlank(position));
        RectF rect = document.getPageOriginSize(position);
        assertTrue(rect.width() > 0);
        assertTrue(rect.height() > 0);
        document.close();
    }

    public void testImagePluginPngDraw() throws Exception {
        final String path = "/mnt/sdcard/Pictures/normal3.png";
        ReaderPlugin plugin = new ImagesReaderPlugin(getActivity(), null);
        ReaderDocument document = plugin.open(path, null, null);
        assertNotNull(document);
        final ReaderView view = document.getView(null);
        assertNotNull(view);
        final ReaderNavigator navigator = view.getNavigator();
        assertNotNull(navigator);
        assertTrue(navigator.getTotalPage() > 0);
        final String position = navigator.getInitPosition();
        assertTrue(StringUtils.isNotBlank(position));
        RectF rect = document.getPageOriginSize(position);
        assertTrue(rect.width() > 0);
        assertTrue(rect.height() > 0);

        ReaderRenderer readerRenderer = view.getRenderer();
        assertNotNull(readerRenderer);

        ReaderBitmapImpl bitmap = ReaderBitmapImpl.create((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
        readerRenderer.draw(String.valueOf(0), 1.0f, 0, bitmap);
        BitmapUtils.saveBitmap(bitmap.getBitmap(), "/mnt/sdcard/imagePlugin.png");
        document.close();
    }

    public void testImagePluginJpegBase1() throws Exception {
        final String path = "/mnt/sdcard/Pictures/normal.jpg";
        ReaderPlugin plugin = new ImagesReaderPlugin(getActivity(), null);
        ReaderDocument document = plugin.open(path, null, null);
        assertNotNull(document);
        final String position = document.getView(null).getNavigator().getPositionByPageName(path);
        RectF rect = document.getPageOriginSize(position);
        assertTrue(TestUtils.compareFloatWhole(rect.width(), 600));
        assertTrue(TestUtils.compareFloatWhole(rect.height(), 800));
        document.close();
    }

}
