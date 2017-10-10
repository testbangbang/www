package com.onyx.android.sample.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.android.sample.R;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;


public class TextUpdateFragment extends BaseTestFragment {

    private TextView textView;
    private GridLayout gridLayout;
    private List<Button> buttonList = new ArrayList<>();
    private StringBuilder text = new StringBuilder();
    private static final String CLEAR = "Clear";

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateViews();
            triggerUpdate();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_text_update, container, false);
        textView = (TextView) view.findViewById(R.id.textView);
        gridLayout = (GridLayout) view.findViewById(R.id.grid_layout);
        addButtons();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        triggerUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTest();
    }

    private void triggerUpdate() {
        handler.postDelayed(runnable, 1);
    }

    @Override
    public void stopTest() {
        super.stopTest();
        handler.removeCallbacks(runnable);
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
        final int N = 10;
        for (int i = 0; i <= N; i++) {
            Button btn = new Button(getActivity());
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
