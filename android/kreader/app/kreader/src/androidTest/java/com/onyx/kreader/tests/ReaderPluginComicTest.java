package com.onyx.kreader.tests;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import com.onyx.android.sdk.api.ReaderBitmap;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.reader.api.ReaderDocument;
import com.onyx.android.sdk.reader.api.ReaderException;
import com.onyx.android.sdk.data.ReaderBitmapImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderDocumentOptionsImpl;
import com.onyx.android.sdk.reader.host.impl.ReaderPluginOptionsImpl;
import com.onyx.android.sdk.reader.plugins.comic.ComicArchiveZip;
import com.onyx.android.sdk.reader.plugins.comic.ComicReaderPlugin;
import com.onyx.android.sdk.reader.plugins.comic.UnrarJniWrapper;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by joy on 3/17/16.
 */
public class ReaderPluginComicTest extends ActivityInstrumentationTestCase2<ReaderTestActivity> {
    private static final String TAG = ReaderPluginComicTest.class.getSimpleName();

    private static final String RAR_FILE = "/mnt/sdcard/Books/a.cbr";
    private static final String ZIP_FILE = "/mnt/sdcard/Books/a.cbz";
    private static final String ZIP_ENCRYPTED_FILE = "/mnt/sdcard/Books/p.cbz";
    private static final String RAR_ENCRYPTED_FILE = "/mnt/sdcard/Books/p1.cbr";
    private static final String RAR_ENCRYPTED_NAME_FILE = "/mnt/sdcard/Books/p2.cbr";

    public ReaderPluginComicTest() {
        super(ReaderTestActivity.class);
    }

    private void testRarJniWrapper(String filePath, String password) {
        File file = new File(filePath);
        assertTrue(file.exists() && file.isFile());
        UnrarJniWrapper wrapper = new UnrarJniWrapper();
        assertTrue(wrapper.open(filePath));
        if (!StringUtils.isNullOrEmpty(password)) {
            wrapper.setPassword(password);
        }
        String[] entries = wrapper.getEntries();
        assertTrue(entries != null && entries.length > 0);
        Log.i(TAG, "entries in file: " + filePath + ", " + entries.length);
        for (String e : entries) {
            byte[] data = wrapper.extractEntryData(e);
            assertNotNull(data);
            Log.i(TAG, "extracting entry: " + e + ", size: " + data.length);
        }
        wrapper.close();
    }

    public void testRarEncrypted() {
        testRarJniWrapper(RAR_FILE, "");
        testRarJniWrapper(RAR_ENCRYPTED_FILE, "boox");
        testRarJniWrapper(RAR_ENCRYPTED_NAME_FILE, "boox");
    }

    private boolean isInvalidZipPassword(Exception e) {
        return e.getMessage().endsWith("Wrong Password?");
    }

    private void helpTestZipWrapper(String filePath, String password, boolean invalidPassword) {
        File file = new File(filePath);
        assertTrue(file.exists() && file.isFile());
        ComicArchiveZip zip = new ComicArchiveZip();
        assertTrue(zip.open(filePath, password));
        List<String> pages = zip.getPageList();
        assertTrue(pages.size() > 0);
        Log.i(TAG, "pages in file: " + filePath + ", " + pages.size());
        boolean checkPassword = true;
        byte[] buf = new byte[4 * 4096];
        for (String p : pages) {
            InputStream s = zip.getPageInputStream(p);
            assertNotNull(s);
            Log.i(TAG, "extracting entry: " + p);
            try {
                if (checkPassword) {
                    checkPassword = false;
                    while (s.read(buf) != -1) {
                    }
                }
                s.close();
            } catch (Exception e) {
//                if (invalidPassword) {
//                    assertTrue(isInvalidZipPassword(e));
//                } else {
//                    assertFalse(isInvalidZipPassword(e));
//                }
                Log.w(TAG, e);
            }
        }
    }

    @Suppress
    public void testZipEncrypted() {
        helpTestZipWrapper(ZIP_FILE, "", false);
        helpTestZipWrapper(ZIP_ENCRYPTED_FILE, "boox", false);
        helpTestZipWrapper(ZIP_ENCRYPTED_FILE, "kkk", true);
    }

    public void testAccept() {
        assertTrue(ComicReaderPlugin.accept(ZIP_FILE));
        assertTrue(ComicReaderPlugin.accept(RAR_FILE));

        assertTrue(ComicReaderPlugin.accept("/mnt/sdcard/Books/a.CBR"));
        assertTrue(ComicReaderPlugin.accept("/mnt/sdcard/Books/a.Cbr"));
        assertTrue(ComicReaderPlugin.accept("/mnt/sdcard/Books/a.CBZ"));
        assertTrue(ComicReaderPlugin.accept("/mnt/sdcard/Books/a.Cbz"));
        assertFalse(ComicReaderPlugin.accept("/mnt/sdcard/Books/CBZ"));
        assertFalse(ComicReaderPlugin.accept("/mnt/sdcard/Books/a.xyz"));

        assertTrue(ComicReaderPlugin.accept("D:\\My Books\\a.CBR"));
        assertTrue(ComicReaderPlugin.accept("D:\\My Books\\a.Cbz"));
        assertFalse(ComicReaderPlugin.accept("D:\\My Books\\CBR"));
        assertFalse(ComicReaderPlugin.accept("D:\\My Books\\a.Xyz"));
    }

    private ComicReaderPlugin createPlugin() {
        return new ComicReaderPlugin(getActivity(), new ReaderPluginOptionsImpl());
    }

    private ReaderDocument openFile(ComicReaderPlugin plugin, String filePath, String password) throws ReaderException {
        File file = new File(filePath);
        assertTrue(file.exists() && file.isFile());
        assertTrue(ComicReaderPlugin.accept(filePath));
        plugin.open(filePath, new ReaderDocumentOptionsImpl(password, password), new ReaderPluginOptionsImpl());
        return plugin;
    }

    private ReaderDocument openFile(String filePath, String password) throws ReaderException {
        ComicReaderPlugin plugin = createPlugin();
        return openFile(plugin, filePath, password);
    }

    private void testOpenFile(String filePath, String password) throws ReaderException {
        ReaderDocument doc = openFile(filePath, password);
        assertNotNull(doc);
        doc.close();
    }

    private static class TestCase {
        public String path;
        public String password;

        public TestCase(String path, String password) {
            this.path = path;
            this.password = password;
        }
    }

    List<TestCase> testCases = Arrays.asList((new TestCase[]{
//            new TestCase(ZIP_FILE, null),
//            new TestCase(ZIP_FILE, ""),
//            new TestCase(ZIP_ENCRYPTED_FILE, "boox"),
            new TestCase(RAR_FILE, null),
            new TestCase(RAR_FILE, ""),
            new TestCase(RAR_ENCRYPTED_FILE, "boox"),
            new TestCase(RAR_ENCRYPTED_NAME_FILE, "boox"),
    }));

    public void testOpenFile() throws ReaderException {
        for (TestCase test : testCases) {
            testOpenFile(test.path, test.password);
        }
    }

    private void testCleanUp(String filePath, String password) throws ReaderException {
        ComicReaderPlugin plugin = createPlugin();
        for (int i = 0; i < 3; i++) {
            ReaderDocument doc = openFile(plugin, filePath, password);
            assertNotNull(doc);
            doc.close();
        }
    }

    public void testCleanUp() throws ReaderException {
        for (TestCase test : testCases) {
            testCleanUp(test.path, test.password);
        }
    }

    private void testPageCount(String filePath, String password) throws ReaderException {
        ReaderDocument doc = openFile(filePath, password);
        assertTrue(doc != null && ((ComicReaderPlugin)doc).getTotalPage() > 0);
        doc.close();
    }

    public void testPageCount() throws ReaderException {
        for (TestCase test : testCases) {
            testPageCount(test.path, test.password);
        }
    }

    private void testPageRender(String filePath, String password) throws ReaderException {
        ComicReaderPlugin plugin = (ComicReaderPlugin)openFile(filePath, password);
        String position = String.valueOf(plugin.getTotalPage() - 1);
        RectF rect = plugin.getPageOriginSize(position);
        assertTrue(rect != null && rect.width() > 0 && rect.height() > 0);
        ReaderBitmap bitmap = ReaderBitmapImpl.create((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
        assertTrue(bitmap != null && bitmap.getBitmap() != null);
        assertTrue(plugin.draw(position, 1.0f, 0, bitmap.getBitmap(), rect, rect, rect));
        plugin.close();
    }

    public void testPageRender() throws ReaderException {
        for (TestCase test : testCases) {
            testPageRender(test.path, test.password);
        }
    }

}
