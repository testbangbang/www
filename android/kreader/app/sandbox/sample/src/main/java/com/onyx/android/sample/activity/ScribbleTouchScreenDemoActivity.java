package com.onyx.android.sample.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.onyx.android.sample.R;
import com.onyx.android.sample.device.DeviceConfig;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.data.TouchPoint;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScribbleTouchScreenDemoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PEN_STOP = 0;
    private static final int PEN_START = 1;
    private static final int PEN_DRAWING = 2;
    private static final int PEN_PAUSE = 3;

    @Bind(R.id.button_pen)
    Button buttonPen;
    @Bind(R.id.button_eraser)
    Button buttonEraser;
    @Bind(R.id.button_save)
    Button buttonSave;

    @Bind(R.id.surfaceview)
    SurfaceView surfaceView;

    boolean scribbleMode = false;
    List<TouchPoint> points = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scribble_touch_screen_demo);

        verifyStoragePermissions(this);

        ButterKnife.bind(this);
        buttonPen.setOnClickListener(this);
        buttonEraser.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                cleanSurfaceView();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                cleanSurfaceView();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                // ignore multi touch
                if (e.getPointerCount() > 1) {
                    return false;
                }

                final float baseWidth = 5;

                switch (e.getAction() & MotionEvent.ACTION_MASK) {
                    case (MotionEvent.ACTION_DOWN):
                        points.add(new TouchPoint(e));
                        return true;
                    case (MotionEvent.ACTION_CANCEL):
                    case (MotionEvent.ACTION_OUTSIDE):
                        break;
                    case MotionEvent.ACTION_UP:
                        points.add(new TouchPoint(e));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int n = e.getHistorySize();
                        for(int i = 0; i < n; ++i) {
                            points.add(TouchPoint.fromHistorical(e, i));
                        }
                        points.add(new TouchPoint(e));
                        return true;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        leaveScribbleMode();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        leaveScribbleMode();
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(buttonPen)) {
            enterScribbleMode();
            return;
        } else if (v.equals(buttonEraser)) {
            leaveScribbleMode();
            cleanSurfaceView();
            return;
        } else if (v.equals(buttonSave)) {
            leaveScribbleMode();
            savePoints();
        }
    }

    private void cleanSurfaceView() {
        if (surfaceView.getHolder() == null) {
            return;
        }
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.WHITE);
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private void enterScribbleMode() {
        scribbleMode = true;
        EpdController.setStrokeWidth(3.0f);
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_DRAWING);
    }

    private void leaveScribbleMode() {
        scribbleMode = false;
        EpdController.setScreenHandWritingPenState(surfaceView, PEN_STOP);
    }

    private void savePoints() {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter("/mnt/sdcard/points.txt");
            String newLine = System.getProperty("line.separator");
            for(TouchPoint touchPoint : points) {
                fileWriter.write("" + touchPoint.x + " " + touchPoint.y + newLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FileUtils.closeQuietly(fileWriter);
            Log.e("###########", "save points finished");
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
