package com.onyx.android.sdk;

import android.app.Application;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;
import com.onyx.android.sdk.utils.TestUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by zhuzeng on 16/04/2017.
 */

public class ThumbnailTest  extends ApplicationTestCase<Application> {

    private static boolean dbInit = false;

    public ThumbnailTest() {
        super(Application.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createApplication();
    }

    public static String testFolder() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    private void clearTestFolder() {
        FileUtils.purgeDirectory(new File(testFolder()));
        FileUtils.mkdirs(testFolder());
    }


    public void testSaveAndGet() throws Exception {
        clearTestFolder();
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        final ContentResolver resolver = getContext().getContentResolver();
        String imgSaved = null;
        try {
            imgSaved = MediaStore.Images.Media.insertImage(
                    resolver,
                    bitmap,
                    UUID.randomUUID().toString() + ".png",
                    "drawing");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assertNotNull(imgSaved);
    }
}
