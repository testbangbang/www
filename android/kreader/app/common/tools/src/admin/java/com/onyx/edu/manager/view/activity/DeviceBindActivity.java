package com.onyx.edu.manager.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.data.model.v2.GroupUserInfo;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.event.DataRefreshEvent;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.view.fragment.DeviceBindCommitFragment;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by suicheng on 2017/7/17.
 */
public class DeviceBindActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_bind);
        postOnCreated();
    }

    private void postOnCreated() {
        GroupUserInfo groupUserInfo = JSONObjectParseUtils.parseObject(getIntent().getStringExtra(ContentManager.KEY_GROUP_USER_INFO),
                GroupUserInfo.class);
        showFragment(DeviceBindCommitFragment.newInstance(groupUserInfo, false));
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.id_content, fragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().post(new DataRefreshEvent());
        super.onDestroy();
    }
}
