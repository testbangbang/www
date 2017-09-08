package com.onyx.android.sample;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.onyx.android.sdk.api.device.EpdDeviceManager;
import com.onyx.android.sdk.utils.TestUtils;

/**
 * Created by wangxu on 17-8-2.
 */

public class FastUpdateModeActivity extends AppCompatActivity {
    
    private boolean isFastMode = false;
    private CountDownTimer timer;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_update_mode);
        textView = (TextView) findViewById(R.id.textView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
    }

    private long generateRandomTime() {
        return TestUtils.randInt(1, 10) * 1000;
    }

    private void toggleFastMode () {
        textView.setText(isFastMode ? "quit fast update mode..." : "enter fast update mode...");
        if (isFastMode) {
            EpdDeviceManager.exitAnimationUpdate(true);
        } else {
            EpdDeviceManager.enterAnimationUpdate(true);
        }
        isFastMode = !isFastMode;
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(generateRandomTime(), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textView.setText(getText(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                toggleFastMode();
                startTimer();
            }
        };
        timer.start();
    }

    private String getText(long time) {
        return "After " + time / 1000 + "s will " + (isFastMode ? "quit " : "enter ") + "fast update mode.";
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
        if (isFastMode) {
            EpdDeviceManager.exitAnimationUpdate(true);
        }
    }
}
