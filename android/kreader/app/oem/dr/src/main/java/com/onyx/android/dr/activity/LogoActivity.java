package com.onyx.android.dr.activity;

import android.view.KeyEvent;
import android.view.View;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.data.MenuBean;
import com.onyx.android.dr.event.AccountAvailableEvent;
import com.onyx.android.dr.event.LoginFailedEvent;
import com.onyx.android.dr.event.WifiConnectedEvent;
import com.onyx.android.dr.presenter.MainPresenter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hehai on 17-9-9.
 */

public class LogoActivity extends BaseActivity implements MainView {

    private MainPresenter mainPresenter;

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_logo;
    }

    @Override
    protected void initConfig() {
        StopBootAnimation();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        mainPresenter = new MainPresenter(this);
        mainPresenter.loadData(this);
        mainPresenter.authToken();
    }

    @Override
    public void setTabMenuData(List<MenuBean> menuData) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginFailedEvent(LoginFailedEvent event) {
        ActivityManager.startLoginActivity(this);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAccountAvailableEvent(AccountAvailableEvent event) {
        ActivityManager.startMainActivity(this);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onWifiConnectedEvent(WifiConnectedEvent event) {
        mainPresenter.authToken();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void StopBootAnimation() {
        try {
            Method method = View.class.getDeclaredMethod("requestStopBootAnimation", null);
            method.setAccessible(true);
            method.invoke(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
