package com.onyx.phone.reader.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.onyx.phone.reader.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ming on 2017/4/20.
 */

public class StartActivity extends AppCompatActivity {

    @Bind(R.id.start_view)
    ImageView startView;

    private static final long START_DELAY_TIME = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);
        startView.postDelayed(new Runnable() {
            @Override
            public void run() {
                enterMainActivity();
            }
        }, START_DELAY_TIME);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void enterMainActivity() {
        Intent intent = new Intent(StartActivity.this,
                MainActivity.class);
        startActivity(intent);
        this.overridePendingTransition(R.anim.enteralpha,R.anim.exitalpha);
        finish();
    }
}
