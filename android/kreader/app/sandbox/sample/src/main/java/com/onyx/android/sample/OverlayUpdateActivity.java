package com.onyx.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by wangxu on 17-8-3.
 */

public class OverlayUpdateActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new OverlaySurfaceView(this));
    }

}
