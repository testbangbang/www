package com.onyx.android.sdk.reader.tests;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.test.ApplicationTestCase;
import android.util.DisplayMetrics;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.reader.reflow.ImageReflowSettings;
import com.onyx.android.sdk.reader.utils.ImageUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by joy on 10/20/16.
 */
public class ImageReflowTest extends ApplicationTestCase<Application> {
    public ImageReflowTest() {
        super(Application.class);
    }

    private Bitmap loadBitmapFromStream(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferQualityOverSpeed = true;
        options.inMutable = true; // set mutable to be true, so we can always get a copy of the bitmap with Bitmap.createBitmap()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(is, null, options);
    }

    private Bitmap loadBitmapFromLocalFile() throws FileNotFoundException {
        return loadBitmapFromStream(new FileInputStream("/extsd/Pictures/reflowin.bmp"));
    }

    public void testReflow() throws FileNotFoundException {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();

        Bitmap bitmap = loadBitmapFromLocalFile();
        ImageReflowSettings settings = ImageReflowSettings.createSettings();
        settings.dev_width = displayMetrics.widthPixels;
        settings.dev_height = displayMetrics.heightPixels;
        Debug.e(getClass(), "display: [%d, %d]", displayMetrics.widthPixels, displayMetrics.heightPixels);

        Benchmark benchmark = new Benchmark();
        assertTrue(ImageUtils.reflowPage("1", bitmap, settings));
        benchmark.report("reflow bitmap finished");
        int[] size = new int[2];
        assertTrue(ImageUtils.getReflowedPageSize("1", size));
        Debug.d(getClass(), "reflowed page: [%d, %d]", size[0], size[1]);

        int pageWidth = size[0];
        int pageHeight = size[1];
        int displayHeight = settings.dev_height;
        Bitmap subPage = Bitmap.createBitmap(settings.dev_width, settings.dev_height, Bitmap.Config.ARGB_8888);
        int i = 0;
        benchmark.restart();
        for (int top = 0; top < pageHeight; top += displayHeight) {
            int bottom = Math.min(pageHeight, top + displayHeight);
            subPage.eraseColor(Color.WHITE);
            assertTrue(ImageUtils.renderReflowedPage("1", 0, top, pageWidth, bottom, subPage));
            benchmark.report("rendering sub page: " + i);
            BitmapUtils.saveBitmap(subPage, String.format("/sdcard/subpage%d.png", i));
            i++;
        }

        ImageUtils.releaseReflowedPages();
    }
}
