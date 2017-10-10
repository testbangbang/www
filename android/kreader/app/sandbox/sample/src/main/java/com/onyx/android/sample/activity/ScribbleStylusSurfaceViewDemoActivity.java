package com.onyx.android.sample.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sample.view.MyView;
import com.onyx.android.sample.R;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.scribble.api.TouchHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScribbleStylusSurfaceViewDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Bind(R.id.button_pen)
    Button buttonPen;
    @Bind(R.id.button_eraser)
    Button buttonEraser;
    @Bind(R.id.surfaceview)
    MyView surfaceView;

    boolean scribbleMode = false;
    private EventBus eventBus = new EventBus();
    private TouchHelper touchHelper;
    private int color = Color.LTGRAY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble_surfaceview_stylus_demo);

        ButterKnife.bind(this);
        buttonPen.setOnClickListener(this);
        buttonEraser.setOnClickListener(this);

        initSurfaceView();
    }

    private void initSurfaceView() {
        touchHelper = new TouchHelper(eventBus);
        surfaceView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                List<Rect> exclude = new ArrayList<>();
                exclude.add(touchHelper.getRelativeRect(surfaceView, buttonEraser));
                exclude.add(touchHelper.getRelativeRect(surfaceView, buttonPen));

                Rect limit = new Rect();
                surfaceView.getLocalVisibleRect(limit);
                cleanSurfaceView();
                touchHelper.setup(surfaceView)
                        .setStrokeWidth(3.0f)
                        .setUseRawInput(true)
                        .setLimitRect(limit, exclude)
                        .startRawDrawing();
            }
        });
    }

    @Override
    protected void onDestroy() {
        leaveScribbleMode();
        touchHelper.quit();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        touchHelper.pauseRawDrawing();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonPen)) {
            penStart();
            return;
        } else if (v.equals(buttonEraser)) {
            leaveScribbleMode();
            cleanSurfaceView();
            return;
        }
    }

    private void cleanSurfaceView() {
        surfaceView.setBkColor(Color.WHITE);
        EpdController.invalidate(surfaceView, UpdateMode.GC);
    }

    private void enterScribbleMode() {
        EpdController.enterScribbleMode(surfaceView);
        scribbleMode = true;
    }

    private void penStart() {
        touchHelper.resumeRawDrawing();
    }

    private void leaveScribbleMode() {
        scribbleMode = false;
        EpdController.leaveScribbleMode(surfaceView);
        touchHelper.pauseRawDrawing();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

}
