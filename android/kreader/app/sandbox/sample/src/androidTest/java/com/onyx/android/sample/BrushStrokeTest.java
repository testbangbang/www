package com.onyx.android.sample;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.test.InstrumentationTestCase;

import com.onyx.android.sample.scribble.Stroke;
import com.onyx.android.sample.scribble.StrokePoint;
import com.onyx.android.sample.scribble.StrokeRenderer;
import com.onyx.android.sdk.utils.Benchmark;

import java.util.Random;

/**
 * Created by joy on 12/22/17.
 */

public class BrushStrokeTest extends InstrumentationTestCase {

    public void test() {
        int width = 1000;
        int height = 1000;
        float dist = (float)Math.sqrt(width * width + height * height);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Random random = new Random(System.currentTimeMillis());

        StrokeRenderer renderer = new StrokeRenderer(new Stroke());

        Benchmark.globalBenchmark().restart();
        int N = 10000;
        for (int i = 0; i < N; i++) {
            float x = (i * dist) / N;
            float y = (i * dist) / N;

            renderer.addPoint(canvas, new StrokePoint(x, y, -1.0f, random.nextFloat(),
                    System.currentTimeMillis(), 2));
        }
        Benchmark.globalBenchmark().reportError("after draw stroke");
    }
}
