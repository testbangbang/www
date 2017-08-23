package com.onyx.edu.manager.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.edu.manager.R;
import com.onyx.edu.manager.event.GroupSelectEvent;
import com.onyx.edu.manager.manager.ContentManager;
import com.onyx.edu.manager.view.fragment.GroupListFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by suicheng on 2017/8/14.
 */
public class GroupListSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        postOnCreated();
    }

    private void postOnCreated() {
        showFragment(GroupListFragment.newInstance());
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.id_content, fragment);
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGroupSelectEvent(GroupSelectEvent event) {
        Intent intent = new Intent();
        intent.putExtra(ContentManager.KEY_GROUP_SELECT, String.valueOf(JSONObjectParseUtils.toJson(event.group)));
        setResult(RESULT_OK, intent);
        finish();
    }
}
