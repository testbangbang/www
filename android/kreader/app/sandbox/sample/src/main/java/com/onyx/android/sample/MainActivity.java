package com.onyx.android.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sdk.api.device.epd.EpdController;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.button_environment)
    Button buttonEnvironment;
    @Bind(R.id.button_epd)
    Button buttonEpd;
    @Bind(R.id.button_front_light)
    Button buttonFrontLight;
    @Bind(R.id.button_touch_screen_scribble)
    Button buttonTouchScreenScribble;
    @Bind(R.id.button_surfaceview_stylus_scribble)
    Button buttonStylusScribble;
    @Bind(R.id.button_webview_stylus_scribble)
    Button buttonStylusWebviewScribble;
    @Bind(R.id.button_full_screen)
    Button buttonFullScreen;
    @Bind(R.id.button_scribble_state)
    Button buttonScribbleState;
    @Bind(R.id.button_parallel_update)
    Button buttonParallelUpdate;
    @Bind(R.id.button_fast_update)
    Button buttonFastUpdate;
    @Bind(R.id.button_overlay_update)
    Button buttonOverlayUpdate;
    @Bind(R.id.button_text_select)
    Button buttonTextSelect;
    @Bind(R.id.button_rect_update)
    Button buttonRectUpdate;
    @Bind(R.id.button_image_diff)
    Button buttonImageDiff;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        buttonEnvironment.setOnClickListener(this);
        buttonEpd.setOnClickListener(this);
        buttonFrontLight.setOnClickListener(this);
        buttonTouchScreenScribble.setOnClickListener(this);
        buttonStylusScribble.setOnClickListener(this);
        buttonFullScreen.setOnClickListener(this);
        buttonStylusWebviewScribble.setOnClickListener(this);
        buttonScribbleState.setOnClickListener(this);
        buttonParallelUpdate.setOnClickListener(this);
        buttonFastUpdate.setOnClickListener(this);
        buttonOverlayUpdate.setOnClickListener(this);
        buttonTextSelect.setOnClickListener(this);
        buttonRectUpdate.setOnClickListener(this);
        buttonImageDiff.setOnClickListener(this);
        final View view = findViewById(android.R.id.content);
        EpdController.enablePost(view, 1);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonEnvironment)) {
            startActivity(new Intent(this, EnvironmentDemoActivity.class));
            return;
        } else if (v.equals(buttonEpd)) {
            startActivity(new Intent(this, EpdDemoActivity.class));
            return;
        } else if (v.equals(buttonFrontLight)) {
            startActivity(new Intent(this, FrontLightDemoActivity.class));
            return;
        } else if (v.equals(buttonTouchScreenScribble)) {
            startActivity(new Intent(this, ScribbleTouchScreenDemoActivity.class));
            return;
        } else if (v.equals(buttonStylusScribble)) {
            startActivity(new Intent(this, ScribbleStylusSurfaceViewDemoActivity.class));
            return;
        } else if (v.equals(buttonFullScreen)) {
            startActivity(new Intent(this, FullScreenDemoActivity.class));
        } else if (v.equals(buttonStylusWebviewScribble)) {
            startActivity(new Intent(this, ScribbleStylusWebViewDemoActivity.class));
        } else if (v.equals(buttonScribbleState)) {
            startActivity(new Intent(this, ScribbleStateDemoActivity.class));
        } else if (v.equals(buttonParallelUpdate)) {
            startActivity(new Intent(this, ParallelUpdateActivity.class));
        } else if (v.equals(buttonFastUpdate)) {
            startActivity(new Intent(this, FastUpdateModeActivity.class));
        } else if (v.equals(buttonOverlayUpdate)) {
            startActivity(new Intent(this, OverlayUpdateActivity.class));
        } else if (v.equals(buttonTextSelect)) {
            startActivity(new Intent(this, TextSelectionActivity.class));
        } else if (v.equals(buttonRectUpdate)) {
            startActivity(new Intent(this, RectangleUpdateTest.class));
        } else if (v.equals(buttonImageDiff)) {
            startActivity(new Intent(this, ImageDiffActivity.class));
        }
    }

    @OnClick(R.id.button_sdk_data_ota_test)
    void onSdkDataTestClick() {
        startActivity(new Intent(this, SdkDataOTATestActivity.class));
    }

    @OnClick(R.id.button_settings)
    void settingsClick() {
        startActivity(new Intent(this, SettingsDemoActivity.class));
    }
}
