package com.onyx.android.dr.activity;

import android.view.KeyEvent;
import android.view.View;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.common.ActivityManager;
import com.onyx.android.dr.data.MenuBean;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hehai on 17-9-9.
 */
public class LogoActivity extends BaseActivity implements MainView {
    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_logo;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        if(DRApplication.getInstance().isLoginSuccess()){
            ActivityManager.startMainActivity(this);
        }else{
            ActivityManager.startLoginActivity(this);
        }
        stopBootAnimation();
        finish();
    }

    @Override
    public void setTabMenuData(List<MenuBean> menuData) {

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void stopBootAnimation() {
        try {
            Method method = View.class.getDeclaredMethod("requestStopBootAnimation", null);
            method.setAccessible(true);
            method.invoke(null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
