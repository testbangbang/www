package con.onyx.android.libsetting.view.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import com.onyx.android.sdk.ui.activity.OnyxAppCompatActivity;

import con.onyx.android.libsetting.R;
import con.onyx.android.libsetting.databinding.ActivityApplicationSettingBinding;

public class ApplicationSettingActivity extends OnyxAppCompatActivity {
    ActivityApplicationSettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_setting);
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_application_setting);
    }
}
