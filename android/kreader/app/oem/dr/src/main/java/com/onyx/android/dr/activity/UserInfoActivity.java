package com.onyx.android.dr.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.util.DRPreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by huxiaomao on 17/9/20.
 */

public class UserInfoActivity extends BaseActivity {
    @Bind(R.id.user_info_user_name)
    TextView userInfoUserName;
    @Bind(R.id.user_info_exit)
    TextView userInfoExit;

    @Override
    protected Integer getLayoutId() {
        return R.layout.user_info;
    }

    @Override
    protected void initConfig() {

    }

    @Override
    protected void initView() {
        String account = DRPreferenceManager.getUserAccount(this, null);
        if (StringUtils.isNotBlank(account)) {
            userInfoUserName.setText(account);
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onObject(Object event) {
        super.onObject(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.user_info_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_info_exit:
                onExitSignOut();
                break;
        }
    }

    private void onExitSignOut(){
        if(ActivityManager.startLoginActivity(UserInfoActivity.this)) {
            DRApplication.getInstance().setLogin(false);
            DRPreferenceManager.cleanUserInfo(UserInfoActivity.this);
            finish();
        }
    }
}
