package com.onyx.android.sample.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.SurfaceView;

import com.onyx.android.sample.R;
import com.onyx.android.sample.device.DeviceConfig;
import com.onyx.android.sdk.api.device.epd.EpdController;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by joy on 7/26/17.
 */

public class ScribbleStateDemoActivity extends Activity {

    private static final int PEN_STOP = 0;
    private static final int PEN_START = 1;
    private static final int PEN_DRAWING = 2;
    private static final int PEN_PAUSE = 3;

    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scribble_state_demo);

        ButterKnife.bind(this);


        EpdController.setScreenHandWritingPenState(surfaceView, PEN_START);
    }

    @Override
    protected void onPause() {
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_PAUSE);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_STOP);

        super.onDestroy();
    }

    @OnClick(R.id.button_pen)
    void onButtonPenClicked() {
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_DRAWING);
        setScribbleRegion(new Rect[] { new Rect(0, 0, surfaceView.getWidth(), surfaceView.getHeight())});
    }

    @OnClick(R.id.button_eraser)
    void onButtonEraserClicked() {
        // test stop, when resume, it automatically starts pen.
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_STOP);
        surfaceView.invalidate();
    }

    @OnClick(R.id.button_portrait)
    void onButtonRotatePortrait() {
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_STOP);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @OnClick(R.id.button_landscape)
    void onButtonRotateLandscape() {
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_STOP);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void setScribbleRegion(Rect[] regionList) {
        EpdController.setScreenHandWritingRegionLimit(surfaceView, regionList);
    }


}
