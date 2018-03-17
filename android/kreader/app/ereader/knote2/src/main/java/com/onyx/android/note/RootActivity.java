package com.onyx.android.note;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.onyx.android.note.databinding.ActivityRootBinding;
import com.onyx.android.note.note.scribble.ScribbleFragment;
import com.onyx.android.sdk.utils.DeviceUtils;

import me.yokeyword.fragmentation.SupportActivity;

/**
 * Created by lxm on 2018/2/2.
 */

public class RootActivity extends SupportActivity {

    boolean fullScreen = true;
    private ActivityRootBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.setFullScreenOnCreate(this, fullScreen);
        hideActionBar();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_root);
        loadRootFragment(R.id.root_view, ScribbleFragment.newInstance());
    }

    private void hideActionBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        DeviceUtils.setFullScreenOnResume(this, fullScreen);
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        System.exit(0);
    }
}
