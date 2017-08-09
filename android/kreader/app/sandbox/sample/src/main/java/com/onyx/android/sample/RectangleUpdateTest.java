package com.onyx.android.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wangxu on 17-8-8.
 */

public class RectangleUpdateTest extends AppCompatActivity implements View.OnClickListener{

    @Bind(R.id.a_button)
    Button modeAButton;
    @Bind(R.id.b_button)
    Button modeBButton;
    @Bind(R.id.c_button)
    Button modeCButton;
    @Bind(R.id.d_button)
    Button modeDButton;
    @Bind(R.id.e_button)
    Button modeEButton;

    @Bind(R.id.rect_surfaceView)
    RectangleSurfaceView surfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rectangle_update);

        ButterKnife.bind(this);
        modeAButton.setOnClickListener(this);
        modeAButton.setTag(RectangleSurfaceView.UpdateMode.A);
        modeBButton.setOnClickListener(this);
        modeBButton.setTag(RectangleSurfaceView.UpdateMode.B);
        modeCButton.setOnClickListener(this);
        modeCButton.setTag(RectangleSurfaceView.UpdateMode.C);
        modeDButton.setOnClickListener(this);
        modeDButton.setTag(RectangleSurfaceView.UpdateMode.D);
        modeEButton.setOnClickListener(this);
        modeEButton.setTag(RectangleSurfaceView.UpdateMode.E);
    }

    @Override
    public void onClick(View v) {
        surfaceView.setUpdateMode((RectangleSurfaceView.UpdateMode) v.getTag());
    }
}
