package com.onyx.android.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sample.activity.BrushActivity;
import com.onyx.android.sample.activity.CalligraphyActivity;
import com.onyx.android.sample.activity.EnvironmentDemoActivity;
import com.onyx.android.sample.activity.EpdDemoActivity;
import com.onyx.android.sample.activity.WacomActivity;
import com.onyx.android.sample.fragment.RectangleUpdateFragment;
import com.onyx.android.sample.activity.RefreshTestActivity;
import com.onyx.android.sample.activity.FrontLightDemoActivity;
import com.onyx.android.sample.activity.FullScreenDemoActivity;
import com.onyx.android.sample.activity.ImageDiffActivity;
import com.onyx.android.sample.fragment.ParallelUpdateFragment;
import com.onyx.android.sample.activity.ScribbleStateDemoActivity;
import com.onyx.android.sample.activity.ScribbleStylusSurfaceViewDemoActivity;
import com.onyx.android.sample.activity.ScribbleStylusWebViewDemoActivity;
import com.onyx.android.sample.activity.ScribbleTouchScreenDemoActivity;
import com.onyx.android.sample.activity.SdkDataOTATestActivity;
import com.onyx.android.sample.activity.SettingsDemoActivity;
import com.onyx.android.sdk.api.device.epd.EpdController;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.button_wacom)
    Button buttonWacom;


    @Bind(R.id.button_brush)
    Button buttonBrush;


    @Bind(R.id.button_calligraphy)
    Button buttonCalligraphy;

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
    @Bind(R.id.button_refresh_test)
    Button buttonRefreshTest;
    @Bind(R.id.button_image_diff)
    Button buttonImageDiff;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        buttonWacom.setOnClickListener(this);
        buttonBrush.setOnClickListener(this);
        buttonCalligraphy.setOnClickListener(this);
        buttonEnvironment.setOnClickListener(this);
        buttonEpd.setOnClickListener(this);
        buttonFrontLight.setOnClickListener(this);
        buttonTouchScreenScribble.setOnClickListener(this);
        buttonStylusScribble.setOnClickListener(this);
        buttonFullScreen.setOnClickListener(this);
        buttonStylusWebviewScribble.setOnClickListener(this);
        buttonScribbleState.setOnClickListener(this);
        buttonRefreshTest.setOnClickListener(this);
        buttonImageDiff.setOnClickListener(this);
        final View view = findViewById(android.R.id.content);
        EpdController.enablePost(view, 1);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonWacom)) {
            startActivity(new Intent(this, WacomActivity.class));
            return;
        } else if (v.equals(buttonBrush)) {
            startActivity(new Intent(this, BrushActivity.class));
            return;
        } else if (v.equals(buttonCalligraphy)) {
            startActivity(new Intent(this, CalligraphyActivity.class));
            return;
        } else if (v.equals(buttonEnvironment)) {
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
        } else if (v.equals(buttonImageDiff)) {
            startActivity(new Intent(this, ImageDiffActivity.class));
        } else if (v.equals(buttonRefreshTest)) {
            startActivity(new Intent(this, RefreshTestActivity.class));
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
