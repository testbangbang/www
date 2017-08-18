package com.onyx.android.dr.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.onyx.android.dr.R;


public class SystemUpdateHistoryActivity extends Activity implements View.OnClickListener {
    private ImageView close;
    private double heightPercentage = 0.65;
    private double widthPercentage= 0.8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_update_history_activity);
        setWindowAttributes();
        initView();
    }

    public void initView() {
        close = (ImageView) findViewById(R.id.system_update_history_close);
        close.setOnClickListener(this);
    }

    public void setWindowAttributes() {
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.height = (int) (display.getHeight() * heightPercentage);
        layoutParams.width = (int) (display.getWidth() * widthPercentage);
        getWindow().setAttributes(layoutParams);

        setFinishOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.system_update_history_close:
                onCloseClick(v);
                break;
        }
    }

    public void onCloseClick(View v) {
        finish();
    }
}
