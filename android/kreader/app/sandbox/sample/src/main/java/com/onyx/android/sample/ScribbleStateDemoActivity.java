package com.onyx.android.sample;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;

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

        setScribbleRegion(new Rect[] { new Rect(0, 0, 300, 300), new Rect(300, 400, 500, 600) });
    }

    @OnClick(R.id.button_eraser)
    void onButtonEraserClicked() {
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_STOP);

        surfaceView.invalidate();
    }

    private float[] mapPoint(float x, float y) {
        x = Math.min(Math.max(0, x), surfaceView.getWidth());
        y = Math.min(Math.max(0, y), surfaceView.getHeight());

        final int viewLocation[] = {0, 0};
        surfaceView.getLocationOnScreen(viewLocation);
        final Matrix viewMatrix = new Matrix();
        DeviceConfig deviceConfig = DeviceConfig.sharedInstance(this, "note");
        viewMatrix.postRotate(deviceConfig.getViewPostOrientation());
        viewMatrix.postTranslate(deviceConfig.getViewPostTx(), deviceConfig.getViewPostTy());

        float screenPoints[] = {viewLocation[0] + x, viewLocation[1] + y};
        float dst[] = {0, 0};
        viewMatrix.mapPoints(dst, screenPoints);
        return dst;
    }

    private void setScribbleRegion(Rect[] regionList) {
        for (int i = 0; i < regionList.length; i++) {
            Rect region = regionList[i];
            float[] leftTop = mapPoint(region.left, region.top);
            float[] rightBottom = mapPoint(region.right, region.bottom);

            int left = (int) Math.min(leftTop[0], rightBottom[0]);
            int top = (int) Math.min(leftTop[1], rightBottom[1]);
            int right = (int) Math.max(leftTop[0], rightBottom[0]);
            int bottom = (int) Math.max(leftTop[1], rightBottom[1]);

            region.set(left, top, right, bottom);
        }

        EpdController.setScreenHandWritingRegionLimit(surfaceView, regionList);
    }
}
