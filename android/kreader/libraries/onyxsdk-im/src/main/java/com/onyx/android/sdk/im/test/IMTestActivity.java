package com.onyx.android.sdk.im.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.sdk.im.IMConfig;
import com.onyx.android.sdk.im.IMManager;
import com.onyx.android.sdk.im.R;

/**
 * Created by ming on 2017/7/14.
 */

public class IMTestActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
    }

    private void init() {

    }
}
