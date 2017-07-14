package com.onyx.android.monitor;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.onyx.android.monitor.databinding.ActivityPreviewBinding;

public class PreviewActivity extends Activity {
    static final String TAG = PreviewActivity.class.getSimpleName();
    static final boolean useA2 = true;
    ActivityPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(binding.container.getId(), PreviewFragment.newInstance())
                    .commit();
        }
        if (useA2){
            Log.e(TAG,"Toggle to A2.");
            Intent changeToA2Intent = new Intent(Constant.A2_ACTION);
            sendBroadcast(changeToA2Intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (useA2){
            Log.e(TAG,"Restore A2 to Normal.");
            Intent restoreNormalUpdateIntent = new Intent(Constant.A2_ACTION);
            sendBroadcast(restoreNormalUpdateIntent);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
