package com.onyx.jdread.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.R;
import com.onyx.jdread.common.BaseFragment;
import com.onyx.jdread.common.ViewConfig;
import com.onyx.jdread.databinding.ActivityMainBinding;
import com.onyx.jdread.event.ChangeChildViewEvent;
import com.onyx.jdread.event.PopCurrentChildViewEvent;
import com.onyx.jdread.event.PushChildViewToStackEvent;
import com.onyx.jdread.model.FunctionBarModel;
import com.onyx.jdread.model.MainViewModel;
import com.onyx.jdread.model.SystemBarModel;
import com.onyx.jdread.shop.ui.StoreFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private FragmentManager fragmentManager = null;
    private FragmentTransaction transaction;
    private BaseFragment currentFragment;
    private String currentChildViewName;
    private Map<String, BaseFragment> childViewList = new HashMap<>();
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initLibrary();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    private void initLibrary() {
        EventBus.getDefault().register(this);
    }

    private void initData() {
        binding.setMainViewModel(new MainViewModel());
        initSystemBar();
        initFunctionBar();
    }

    private void initSystemBar() {
        binding.mainSystemBar.setSystemBarModel(new SystemBarModel());
    }

    private void initFunctionBar() {
        FunctionBarModel functionBarModel = new FunctionBarModel();
        binding.mainFunctionBar.setFunctionBarModel(functionBarModel);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void switchCurrentFragment(String childViewName) {
        if (StringUtils.isNotBlank(currentChildViewName) && currentChildViewName.equals(childViewName)) {
            return;
        }
        initFragmentManager();
        notifyChildViewChangeWindow();
        BaseFragment baseFragment = getPageView(childViewName);

        transaction.replace(R.id.main_content_view, baseFragment);
        transaction.commitAllowingStateLoss();
        saveChildViewInfo(childViewName, baseFragment);
    }

    private void notifyChildViewChangeWindow() {
        if (currentFragment != null) {
            currentFragment.hideWindow();
        }
        if (currentFragment != null && currentFragment.isVisible()) {
            transaction.hide(currentFragment);
        }
    }

    private void initFragmentManager() {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        transaction = fragmentManager.beginTransaction();
    }

    private void saveChildViewInfo(String childViewName, BaseFragment baseFragment) {
        childViewList.put(childViewName, baseFragment);
        currentFragment = baseFragment;
        currentChildViewName = childViewName;
    }

    private BaseFragment getPageView(String childViewName) {
        BaseFragment baseFragment = childViewList.get(childViewName);
        if (baseFragment == null) {
            try {
                Class clazz = Class.forName(childViewName);
                baseFragment = (BaseFragment) clazz.newInstance();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        if (baseFragment != null) {
            setChildViewInfo(baseFragment);
        }
        return baseFragment;
    }

    private void setChildViewInfo(BaseFragment baseFragment) {
        baseFragment.setViewEventCallBack(childViewEventCallBack);
    }

    private BaseFragment.ChildViewEventCallBack childViewEventCallBack = new BaseFragment.ChildViewEventCallBack() {
        @Override
        public void gotoView(String childClassName) {
            PushChildViewToStackEvent event = new PushChildViewToStackEvent();
            event.childClassName = childClassName;
            EventBus.getDefault().post(event);
        }

        @Override
        public void viewBack() {
            PopCurrentChildViewEvent event = new PopCurrentChildViewEvent();
            EventBus.getDefault().post(event);
        }

        @Override
        public void hideOrShowSystemBar(boolean flags) {

        }

        @Override
        public void hideOrShowFunctionBar(boolean flags) {

        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (currentFragment instanceof StoreFragment) {
            GestureDetector gestureDetector = ((StoreFragment) currentFragment).getGestureDetector();
            gestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushChildViewToStackEvent(PushChildViewToStackEvent event) {
        ViewConfig.FunctionModule functionModule = ViewConfig.findChildViewParentId(event.childClassName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopCurrentChildViewEvent(PopCurrentChildViewEvent event) {
        String childClassName = currentFragment.getClass().getName();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeChildViewEvent(ChangeChildViewEvent event) {
        switchCurrentFragment(event.childViewName);
    }
}
