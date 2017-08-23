package com.onyx.android.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;


public class TextUpdateActivity extends AppCompatActivity {

    private List<Button> buttonList = new ArrayList<>();
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView textView;
    private StringBuilder text = new StringBuilder();
    private static final String CLEAR = "Clear";

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateViews();
            triggerUpdate();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_update);

        textView = (TextView) findViewById(R.id.textView);
        addButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        triggerUpdate();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private void triggerUpdate() {
        handler.postDelayed(runnable, 1);
    }

    private void updateViews() {
        final int index = TestUtils.randInt(0, buttonList.size() - 1);
        Button button = buttonList.get(index);
        requestFocusButton(button);
        updateText(button.getText().toString());
    }

    private void updateText(final String str) {
        if (CLEAR.equals(str)) {
            text = new StringBuilder();
        } else {
            text.append(str);
        }
        textView.setText(text);
    }

    private void requestFocusButton(final Button button) {
        button.requestFocusFromTouch();
    }

    private void addButtons() {
        GridLayout gridLayout = (GridLayout)findViewById(R.id.grid_layout);
        final int N = 10;
        for (int i = 0; i <= N; i++) {
            Button btn = new Button(this);
            if (i == N) {
                btn.setText(CLEAR);
            } else {
                btn.setText(i + "");
            }
            btn.setBackgroundResource(R.drawable.button_bg);
            btn.setTextSize(30.0f);
            GridLayout.Spec rowSpec = GridLayout.spec(i / 3, 1f);
            GridLayout.Spec columnSpec = GridLayout.spec(i % 3, 1f);
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(rowSpec, columnSpec);
            layoutParams.height = 0;
            layoutParams.width = 0;
            layoutParams.setMargins(80, 80, 80, 80);

            gridLayout.addView(btn, layoutParams);
            buttonList.add(btn);
        }
    }

}
