package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.reader.api.ReaderSelection;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.plugins.djvu.DjvuJniWrapper;
import com.onyx.android.sdk.reader.plugins.djvu.DjvuReaderPlugin;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joy on 3/3/16.
 */
public class ReaderPluginDjvuTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {

    private static final String TAG = ReaderPluginDjvuTest.class.getSimpleName();
    private static final String FILE = "/mnt/sdcard/Books/a.djvu";

    public ReaderPluginDjvuTest() {
        super(ReaderTestActivity.class);
    }

    public void testJniInterface() throws Exception {
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

        ArrayList<ReaderSelection> textChunks = new ArrayList<ReaderSelection>();
        assertTrue(wrapper.extractPageText(page, textChunks));
        for (ReaderSelection chunk : textChunks) {
            assertTrue(chunk.getRectangles().size() == 1);
            Log.i(TAG, "chunk: " + chunk.getText() + ", " + chunk.getRectangles().get(0).toString());
        }

        ArrayList<ReaderSelection> result = new ArrayList<ReaderSelection>();
        assertTrue(wrapper.searchInPage(page, "distance", true, true, result));
        assertTrue(result.size() > 0);
        result.clear();
        assertTrue(wrapper.searchInPage(page, "Distance", true, true, result));
        assertTrue(result.size() == 0);
        result.clear();
        assertTrue(wrapper.searchInPage(page, "dist", true, true, result));
        assertTrue(result.size() == 0);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        assertNotNull(bitmap);
        assertTrue(wrapper.drawPage(page, bitmap, 1.0f, width, height, 0, 0, width, height));

        wrapper.close();
        assertNull(wrapper.getFilePath());
        assertEquals(wrapper.getPageCount(), 0);

        Field field = wrapper.getClass().getDeclaredField("pageTextChunks");
        field.setAccessible(true);
        HashMap<Integer, List<ReaderSelection>> chunks = (HashMap<Integer, List<ReaderSelection>>) field.get(wrapper);
        assertEquals(chunks.size(), 0);
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

    public void testPageRender() throws ReaderException {
        DjvuReaderPlugin plugin = (DjvuReaderPlugin)openTestFile();
        String position = String.valueOf(plugin.getTotalPage() - 1);
        RectF rect = plugin.getPageOriginSize(position);
        assertTrue(rect != null && rect.width() > 0 && rect.height() > 0);
        ReaderBitmap bitmap = ReaderBitmapImpl.create((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
        assertTrue(bitmap != null && bitmap.getBitmap() != null);
        assertTrue(plugin.draw(position, 1.0f, 0, bitmap.getBitmap(), rect, rect, rect));
        plugin.close();
    }
}


