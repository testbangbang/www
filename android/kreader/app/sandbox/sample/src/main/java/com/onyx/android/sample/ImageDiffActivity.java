package com.onyx.android.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.onyx.android.sample.utils.ImageUtils;

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
}
