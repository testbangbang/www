package com.onyx.android.sdk;

import android.app.Application;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.utils.FileUtils;

/**
 * Created by joy on 9/29/16.
 */
public class FileUtilsTest  extends ApplicationTestCase<Application> {
    public FileUtilsTest() {
        super(Application.class);
    }

    public void testGetBaseName() {
        assertEquals(FileUtils.getBaseName("a"), "a");
        assertEquals(FileUtils.getBaseName("a.pdf"), "a");
        assertEquals(FileUtils.getBaseName("a.1.pdf"), "a.1");
        assertEquals(FileUtils.getBaseName("."), "");
    }
}
