package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;

import com.onyx.kreader.api.ReaderBitmap;
import com.onyx.kreader.api.ReaderDocument;
import com.onyx.kreader.api.ReaderException;
import com.onyx.kreader.host.impl.ReaderBitmapImpl;
import com.onyx.kreader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.kreader.plugins.djvu.DjvuJniWrapper;
import com.onyx.kreader.plugins.djvu.DjvuReaderPlugin;

import java.io.File;

/**
 * Created by joy on 3/3/16.
 */
public class ReaderPluginDjvuTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private final static String FILE = "/mnt/sdcard/Books/a.djvu";

    public ReaderPluginDjvuTest() {
        super(ReaderTestActivity.class);
    }

    public void testJniInterface() {
        DjvuJniWrapper wrapper = new DjvuJniWrapper();
        assertTrue(wrapper.open(FILE));
        assertNotNull(wrapper.getFilePath());
        assertTrue(wrapper.getPageCount() > 0);
        int page = wrapper.getPageCount() - 1;
        float[] size = new float[2];
        assertTrue(wrapper.getPageSize(page, size));
        int width = (int)size[0];
        int height = (int)size[1];
        assertTrue(width > 0 && height > 0);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        assertNotNull(bitmap);
        assertTrue(wrapper.drawPage(page, bitmap, 1.0f, width, height, 0, 0, width, height));
        wrapper.close();
    }

    public void testAccept() {
        assertTrue(DjvuReaderPlugin.accept(FILE));
        assertTrue(DjvuReaderPlugin.accept("/mnt/sdcard/Books/a.DJVU"));
        assertTrue(DjvuReaderPlugin.accept("/mnt/sdcard/Books/a.DjVU"));
        assertFalse(DjvuReaderPlugin.accept("/mnt/sdcard/Books/DJVu"));
        assertFalse(DjvuReaderPlugin.accept("/mnt/sdcard/Books/a.pdf"));

        assertTrue(DjvuReaderPlugin.accept("D:\\My Books\\a.DJVU"));
        assertTrue(DjvuReaderPlugin.accept("D:\\My Books\\a.DjVU"));
        assertFalse(DjvuReaderPlugin.accept("D:\\My Books\\DJVu"));
        assertFalse(DjvuReaderPlugin.accept("D:\\My Books\\a.pdf"));
    }

    private ReaderDocument openTestFile(DjvuReaderPlugin plugin) {
        File file = new File(FILE);
        assertTrue(file.exists() && file.isFile());
        assertTrue(DjvuReaderPlugin.accept(FILE));
        try {
           return plugin.open(FILE, null, new ReaderPluginOptionsImpl());
        } catch (ReaderException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ReaderDocument openTestFile() {
        DjvuReaderPlugin plugin = new DjvuReaderPlugin(getActivity(), new ReaderPluginOptionsImpl());
        return openTestFile(plugin);
    }

    public void testOpen() {
        ReaderDocument doc = openTestFile();
        assertNotNull(doc);
        doc.close();
    }

    public void testCleanup() {
        DjvuReaderPlugin plugin = new DjvuReaderPlugin(getActivity(), new ReaderPluginOptionsImpl());
        for (int i = 0; i < 100; i++) {
            ReaderDocument doc = openTestFile(plugin);
            assertNotNull(doc);
            doc.close();
        }
    }

    public void testPageCount() {
        ReaderDocument doc = openTestFile();
        assertTrue(((DjvuReaderPlugin) doc).getTotalPage() > 0);
        doc.close();
    }

    public void testPageRender() {
        DjvuReaderPlugin plugin = (DjvuReaderPlugin)openTestFile();
        String position = String.valueOf(plugin.getTotalPage() - 1);
        RectF rect = plugin.getPageOriginSize(position);
        assertTrue(rect != null && rect.width() > 0 && rect.height() > 0);
        ReaderBitmap bitmap = ReaderBitmapImpl.create((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
        assertNotNull(bitmap);
        assertTrue(plugin.draw(position, 1.0f, 0, bitmap, rect, null, null));
        plugin.close();
    }
}


