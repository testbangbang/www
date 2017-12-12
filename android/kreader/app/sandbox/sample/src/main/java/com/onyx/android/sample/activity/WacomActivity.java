package com.onyx.android.sample.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.onyx.android.sample.R;
import com.onyx.android.sdk.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WacomActivity extends AppCompatActivity {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private int px, py, pressure;
    private boolean pressed;
    private boolean lastPressed;
    private boolean moved;
    private boolean erasing;
    private boolean triggered;
    private boolean readyForSignal;

    private static final int  EV_ABS = 0x03;
    private static final int  ABS_X  = 0x00;
    private static final int  ABS_Y  = 0x01;
    private static final int ABS_PRESSURE = 0x18;
    private static final int EV_SYN = 0;
    private static final int EV_KEY = 1;

    private static final int BTN_TOUCH =  0x14a;
    private static final int BTN_TOOL_PENCIL = 0x143;
    private static final int BTN_TOOL_PEN = 0x140;
    private static final int BTN_TOOL_RUBBER = 0x141;

    private static final float screenWidth = 1404;
    private static final float screenHeight = 1872;

    private static final float touchWidth = 20967;
    private static final float touchHeight = 15725;

    private volatile Path path;

    private float lastX, lastY;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wacom);
        verifyStoragePermissions(this);
        initSurfaceView();
    }

    private void initSurfaceView() {
        surfaceView = (SurfaceView)findViewById(R.id.wacom_surfaceview);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                processData(surfaceHolder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    private void processData(final SurfaceHolder surfaceHolder) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Path> pathList = new ArrayList<>();
                File file = null;
                BufferedReader br;
                try {
                    file = new File("/mnt/sdcard/Books/wacom.txt");
                    br = new BufferedReader(new FileReader(file));
                    String line;
                    int count = 0;
                    while ((line = br.readLine()) != null) {
                        if (parseLine(pathList, line)) {
                            ++count;
                        }
                    }
                    Log.e("####", "line count: "+ count);
                    render(surfaceHolder, pathList);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }
        }});
        thread.start();
    }

    private void render(final SurfaceHolder surfaceHolder, final List<Path> pathList) {
        Canvas canvas = surfaceHolder.lockCanvas();
        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setStrokeWidth(3.0f);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        for(Path path : pathList) {
            canvas.drawPath(path, paint);
        }
        surfaceHolder.unlockCanvasAndPost(canvas);
        Log.e("##########", "Render finished: " + pathList.size());
    }

    private boolean parseLine(final  List<Path> pathList, final String line) {
        if (!line.contains("########### type")) {
            return false;
        }
        Matcher m = Pattern.compile("\\d+").matcher(line);
        List<Integer> numbers = new ArrayList<Integer>();
        while(m.find()) {
            numbers.add(Integer.parseInt(m.group()));
        }
        int size = numbers.size();
        if (size < 6) {
            return false;
        }
        processEvent(pathList, numbers.get(size - 6), numbers.get(size - 5), numbers.get(size - 4));
        return true;
    }


    private void processEvent(final List<Path> pathList, int type, int code, int value) {
        Log.e("########", "process event: " + type + " " + code + " " + value);
        if (type == EV_ABS) {
            if (code == ABS_X) {
                px = value;
            } else if (code == ABS_Y) {
                py = value;
            } else if (code == ABS_PRESSURE) {
                pressure = value;
            }
        } else if (type == EV_SYN) {
            if (pressed) {
                if (!lastPressed) {
                    moved = false;
                    if (moveTo(pathList, px, py)) {
                        lastPressed = true;
                        moved = true;
                    }
                } else {
                    if (!moved) {
                        moved = moveTo(pathList, px, py);
                    }
                    if (moved) {
                        moved = quadTo(px, py);
                    }
                }
            }
        } else if (type == EV_KEY) {
            if (code ==  BTN_TOUCH) {
                erasing = false;
                pressed = value > 0;
                lastPressed = false;
            } else if (code == BTN_TOOL_PENCIL || code == BTN_TOOL_PEN) {
                erasing = false;
            } else if (code == BTN_TOOL_RUBBER) {
                erasing = true;
                pressed = value > 0;
            }
        }
    }

    private boolean moveTo(final List<Path> pathList, int x, int y) {
        float rx = x * screenWidth / touchWidth;
        float ry = y * screenHeight / touchHeight;
        float sx = rx;
        float sy = ry;
        lastX = sx;
        lastY = sy;
        path = new Path();
        path.moveTo(sx, sy);
        pathList.add(path);
        Log.e("#########", "Move to: " + sx + " " + sy);
        return true;
    }

    private boolean quadTo(int x, int y) {
        float rx = x * screenWidth / touchWidth;
        float ry = y * screenHeight / touchHeight;
        float sx = rx;
        float sy = ry;

        float xx = (sx + lastX)  / 2;
        float yy = (sy + lastY)  / 2;
        path.quadTo(xx, yy, sx, sy);
        Log.e("#########", "Quad to: " + sx + " " + sy);
        lastX = sx;
        lastY = sy;
        return true;
    }

}
