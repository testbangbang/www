package com.onyx.jdread.main.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityLockScreenBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.event.PasswordIsCorrectEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.ui.UnlockFragment;
import com.onyx.jdread.main.model.StackList;
import com.onyx.jdread.util.Utils;

import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hehai on 18-1-2.
 */

public class LockScreenActivity extends AppCompatActivity {

    private ActivityLockScreenBinding dataBinding;

    private BaseFragment currentFragment;
    private Map<String, BaseFragment> childViewList = new HashMap<>();
    private StackList stackList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen);
    }

    private void initData() {
        stackList = new StackList();
        ViewConfig.initStackByName(stackList, UnlockFragment.class.getName());
        switchCurrentFragment(stackList.peek());
    }

    private BaseFragment getPageView(String childViewName) {
        BaseFragment baseFragment = childViewList.get(childViewName);
        if (baseFragment == null) {
            try {
                Class clazz = Class.forName(childViewName);
                baseFragment = (BaseFragment) clazz.newInstance();
                baseFragment.setViewEventCallBack(childViewEventCallBack);
            } catch (Exception e) {
                Log.e(getClass().getSimpleName(), "getPageView", e);
            }
        }
        return baseFragment;
    }

    private BaseFragment.ChildViewEventCallBack childViewEventCallBack = new BaseFragment.ChildViewEventCallBack() {
        @Override
        public void gotoView(String childClassName) {
            stackList.push(childClassName);
            switchCurrentFragment(childClassName);
        }

        @Override
        public void viewBack() {
            switchCurrentFragment(stackList.popChildView());
        }

        @Override
        public void hideOrShowSystemBar(boolean flags) {
        }

        @Override
        public void hideOrShowFunctionBar(boolean flags) {
        }
    };

    public void switchCurrentFragment(String childViewName) {
        if (StringUtils.isNullOrEmpty(childViewName)) {
            return;
        }
        BaseFragment baseFragment = getPageView(childViewName);
        if (currentFragment != null) {
            baseFragment.setBundle(currentFragment.getBundle());
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_content_view, baseFragment)
                .commitAllowingStateLoss();
        saveChildViewInfo(childViewName, baseFragment);
    }

    private void saveChildViewInfo(String childViewName, BaseFragment baseFragment) {
        childViewList.put(childViewName, baseFragment);
        currentFragment = baseFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.ensureRegister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.ensureUnregister(SettingBundle.getInstance().getEventBus(), this);
    }

    @Subscribe
    public void onPasswordIsCorrectEvent(PasswordIsCorrectEvent event) {
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return event.getKeyCode() == KeyEvent.KEYCODE_BACK || super.dispatchKeyEvent(event);
    }
}
