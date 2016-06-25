package com.onyx.android.note.activity;

import android.os.Bundle;

import com.onyx.android.note.R;
import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

public class ScribbleActivity extends OnyxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble);
        initSupportActionBar();
    }
}
