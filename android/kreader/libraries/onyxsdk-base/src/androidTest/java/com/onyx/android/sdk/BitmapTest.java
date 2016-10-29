package com.onyx.android.sdk;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.utils.Benchmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by joy on 10/18/16.
 */
public class BitmapTest extends ApplicationTestCase<Application> {
    public BitmapTest() {
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
        return loadBitmapFromStream(new FileInputStream("/extsd/Pictures/TTS.png"));
    }

    private void testEncoderDecoderPerformance(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        Benchmark benchmark = new Benchmark();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        benchmark.restart();
        bitmap.compress(compressFormat, 100, outputStream);
        benchmark.report("compress bitmap to: " + compressFormat + ", size: " + outputStream.size());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        benchmark.restart();
        loadBitmapFromStream(inputStream);
        benchmark.report("decode bitmap from: " + compressFormat);
    }

    public void testEncoderDecoderPerformance() throws FileNotFoundException {
        Benchmark benchmark = new Benchmark();

        Bitmap bitmap = loadBitmapFromLocalFile();
        benchmark.report("loadBitmapFromLocalFile");

        testEncoderDecoderPerformance(bitmap, Bitmap.CompressFormat.PNG);
        testEncoderDecoderPerformance(bitmap, Bitmap.CompressFormat.JPEG);
        testEncoderDecoderPerformance(bitmap, Bitmap.CompressFormat.WEBP);
    }
}
