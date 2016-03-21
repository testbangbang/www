package com.onyx.kreader.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.onyx.kreader.plugins.comic.ComicArchiveZip;
import com.onyx.kreader.plugins.comic.UnrarJniWrapper;
import com.onyx.kreader.utils.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
//        testRarJniWrapper(RAR_FILE, "");
//        testRarJniWrapper(RAR_ENCRYPTED_FILE, "boox");
//        testRarJniWrapper(RAR_ENCRYPTED_NAME_FILE, "boox");
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

    public void testZipEncrypted() {
        helpTestZipWrapper(ZIP_FILE, "", false);
        helpTestZipWrapper(ZIP_ENCRYPTED_FILE, "boox", false);
        helpTestZipWrapper(ZIP_ENCRYPTED_FILE, "kkk", true);
    }

}
