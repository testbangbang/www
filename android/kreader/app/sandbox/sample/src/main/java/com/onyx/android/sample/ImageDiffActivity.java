package com.onyx.android.sample;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.onyx.android.sample.utils.EpdHelper;
import com.onyx.android.sample.utils.ImageUtils;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.FileUtils;

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
        calculateUpdWorkingBuffer();
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
        for(int i = 0; i < 7; ++i) {
            pathList.add("/mnt/sdcard/scp-" + i + ".png");
        }

        EpdHelper epdHelper = new EpdHelper();
        epdHelper.init(pathList);

        while (!epdHelper.isFinished()) {
            epdHelper.merge();
            epdHelper.nextFrame();
        }

        Log.e(TAG, "all finished");
    }



}
