package com.onyx.android.sample.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onyx.android.sample.R;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;


public class ParallelUpdateFragment extends BaseTestFragment {

    private List<Button> buttonList = new ArrayList<>();
    private GridLayout gridLayout;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            updateButton();
            triggerUpdate();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getContext(), R.layout.activity_parallel_update, null);
        gridLayout = (GridLayout) view.findViewById(R.id.grid_layout);
        addButtons();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        triggerUpdate();
    }

    private void triggerUpdate() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, 1);
    }

    private void updateButton() {
        int index = TestUtils.randInt(0, buttonList.size() - 1);
        EpdController.invalidate(buttonList.get(index), UpdateMode.GC);
    }

    private void addButtons() {
        for (int i = 0; i < 60; i++) {
            Button btn = new Button(getActivity());
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

    @Override
    public void stopTest() {
        super.stopTest();
        handler.removeCallbacks(runnable);
    }
}
