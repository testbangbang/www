package com.onyx.android.sample;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.widget.Button;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;


public class ParallelUpdateActivity extends AppCompatActivity {

    private List<Button> buttonList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallel_update);
        addButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        triggerUpdate();
    }

    private void triggerUpdate() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateButton();
                triggerUpdate();
            }
        }, 1);

    }

    private void updateButton() {
        int index = TestUtils.randInt(0, buttonList.size() - 1);
        EpdController.invalidate(buttonList.get(index), UpdateMode.GC);
    }

    private void addButtons() {
        GridLayout gridLayout = (GridLayout)findViewById(R.id.grid_layout);
        for (int i = 0; i < 60; i++) {
            Button btn = new Button(this);
            btn.setId(i);
            final int id_ = btn.getId();
            btn.setText("button " + id_);
            btn.setBackgroundColor(Color.GRAY);
            btn.setTextSize(20.0f);
            GridLayout.Spec rowSpec = GridLayout.spec(i / 5, 1f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % 5, 1f);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.height = 0;
            layoutParams.width = 0;
            layoutParams.setMargins(10, 10, 10, 10);

            gridLayout.addView(btn, layoutParams);
            buttonList.add(btn);
        }
    }



}
