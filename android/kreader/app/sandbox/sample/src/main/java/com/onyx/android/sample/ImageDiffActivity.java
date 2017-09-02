package com.onyx.android.sample;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.onyx.android.sample.utils.EpdHelper;
import com.onyx.android.sample.utils.EpdManager;
import com.onyx.android.sample.utils.ImageUtils;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class ImageDiffActivity extends AppCompatActivity {

    public static final String TAG = ImageDiffActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_diff);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        calculateAllDiffList();
        calculateUpdWorkingBuffer2();
    }

    private void transparentImage() {
        String path = new String("/mnt/sdcard/scp-0.png");
        final Bitmap bitmap = ImageUtils.loadBitmapFromFile(path);
        for(int y = 0; y < bitmap.getHeight() / 4; ++y) {
            for(int x = 0; x < bitmap.getWidth() / 4; ++x) {
                bitmap.setPixel(x, y, Color.TRANSPARENT);
            }
        }
        path = new String("/mnt/sdcard/scp-0-trans.png");
        BitmapUtils.saveBitmap(bitmap, path);


        final Bitmap result = ImageUtils.loadBitmapFromFile(path);
        for(int y = 0; y < result.getHeight() / 4; ++y) {
            for(int x = 0; x < result.getWidth() / 4; ++x) {
                int value = result.getPixel(x, y);
                if (value != Color.TRANSPARENT) {
                    Log.e(TAG, "incorrect value");
                }
            }
        }

        Log.e(TAG, "transparent done");
    }

    private void calculateAllDiffList() {
        int max = 7;
        for(int i = 1; i < max; ++i) {
            String first = new String("/mnt/sdcard/scp-" + (i - 1) + ".png");
            String second = new String("/mnt/sdcard/scp-" + i + ".png");
            String result = new String("/mnt/sdcard/diff-" + i + "-result.png");
            final Bitmap bitmap = ImageUtils.diffImage(first, second);
            BitmapUtils.saveBitmap(bitmap, result);
        }

        // then apply these diffs to origin image to get the final image.
        String first = new String("/mnt/sdcard/scp-0.png");
        Bitmap origin = ImageUtils.loadBitmapFromFile(first);
        for(int i = 1; i < max; ++i) {
            String patchPath = new String("/mnt/sdcard/diff-" + i + "-result.png");
            final Bitmap patch = ImageUtils.loadBitmapFromFile(patchPath);
            origin = ImageUtils.applyDiffImage(origin, patch);
        }
        String finalResult = new String("/mnt/sdcard/final-result-with-patch.png");
        BitmapUtils.saveBitmap(origin, finalResult);
        Log.e(TAG, "apply all patch finished.");
    }

    private void calculateDiff() {
        ImageUtils imageUtils = new ImageUtils();
        List<String> list = new ArrayList<>();
        for(int i = 0; i < 50; ++i) {
            list.add("/mnt/sdcard/scp-" + i + ".png");
        }
        for(int i = 0; i < list.size() - 1; ++i) {
            imageUtils.diff(list.get(i), list.get(i + 1));
            Log.e(TAG, "diff finished: " + list.get(i + 1));
        }

        Log.e(TAG, "All finished");
    }

    private void calculateUpdWorkingBuffer() {
        // load upd list
        List<String> pathList = new ArrayList<>();
        for(int i = 0; i < 6; ++i) {
            pathList.add("/mnt/sdcard/scp-" + i + ".png");
        }

        EpdHelper epdHelper = new EpdHelper();
        epdHelper.init(pathList);

        while (!epdHelper.isFinished()) {
            epdHelper.merge();
            epdHelper.nextFrame();
        }

        final Bitmap finalBitmap = ImageUtils.loadBitmapFromFile("/mnt/sdcard/scp-6.png");
        epdHelper.flush(finalBitmap);
        Log.e(TAG, "all finished with verify result: " + epdHelper.verify());
    }

    private void calculateUpdWorkingBuffer2() {
        // load upd list
        List<String> pathList = new ArrayList<>();
        for(int i = 0; i < 6; ++i) {
            pathList.add("/mnt/sdcard/scp-" + i + ".png");
        }

        EpdManager epdManager = new EpdManager();
        epdManager.init(pathList);

        while (!epdManager.isFinished()) {
            epdManager.merge();
            epdManager.nextWaveformFrame();
            epdManager.nextFramebuffer();
        }

        Log.e(TAG, "all finished with verify result.");
    }



}
