package com.onyx.android.sample.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.onyx.android.sample.R;
import com.onyx.android.sdk.api.device.EpdDeviceManager;
import com.onyx.android.sdk.utils.TestUtils;

/**
 * Created by wangxu on 17-8-2.
 */

public class FastUpdateModeFragment extends BaseTestFragment {
    
    private boolean isFastMode = false;
    private CountDownTimer timer;
    private TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.activity_fast_update_mode, null);
        textView = (TextView) view.findViewById(R.id.textView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        startTest();
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

    public void startTest() {
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
                startTest();
            }
        };
        timer.start();
    }

    private String getText(long time) {
        return "After " + time / 1000 + "s will " + (isFastMode ? "quit " : "enter ") + "fast update mode.";
    }

    public void stopTest() {
        super.stopTest();
        if (timer != null) {
            timer.cancel();
        }
        if (isFastMode) {
            EpdDeviceManager.exitAnimationUpdate(true);
        }
    }
}
