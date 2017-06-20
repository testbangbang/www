package com.onyx.kreader.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.utils.Debug;

/**
 * Created by joy on 3/7/17.
 */

public class OnyxBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Debug.d(getClass(), "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        Debug.d(getClass(), "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Debug.d(getClass(), "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Debug.d(getClass(), "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Debug.d(getClass(), "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Debug.d(getClass(), "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Debug.d(getClass(), "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Debug.d(getClass(), "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Debug.d(getClass(), "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Debug.d(getClass(), "onNewIntent");
        super.onNewIntent(intent);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Debug.d(getClass(), "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }

}
