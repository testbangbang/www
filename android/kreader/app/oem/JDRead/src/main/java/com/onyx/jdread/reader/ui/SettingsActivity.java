package com.onyx.jdread.reader.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivitySettingsBinding;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.Constants;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.event.PopCurrentChildViewEvent;
import com.onyx.jdread.main.event.PushChildViewToStackEvent;
import com.onyx.jdread.main.event.SystemBarClickedEvent;
import com.onyx.jdread.main.event.UpdateTimeFormatEvent;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.main.model.SystemBarModel;
import com.onyx.jdread.main.model.TitleBarModel;
import com.onyx.jdread.main.view.SystemBarPopupWindow;
import com.onyx.jdread.setting.event.FinishSettingEvent;
import com.onyx.jdread.setting.model.SettingBundle;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.shop.event.TopBackEvent;
import com.onyx.jdread.shop.ui.BookDetailFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by hehai on 18-3-18.
 */

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    private ActivitySettingsBinding binding;
    private SystemBarModel systemBarModel;
    private FragmentManager fragmentManager;
    private SystemBarPopupWindow.SystemBarPopupModel systemBarPopupWindowModel;
    private Stack<String> fragmentStack;
    private Map<String, BaseFragment> childViewList = new HashMap<>();
    private long ebookId = Integer.MAX_VALUE;
    private Bundle bundle = new Bundle();
    private boolean showTitle = true;
    private BaseFragment currentFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        getEbookId();
        initSystemBar();
        initTitleBar();
        initFragment();
        initView();
    }

    private void initView(){
        if(ebookId != Integer.MAX_VALUE){
            pushChildViewToStack(BookDetailFragment.class.getName());
            binding.titleBar.getRoot().setVisibility(View.GONE);
            showTitle = false;
        }else {
            pushChildViewToStack(SettingFragment.class.getName());
        }
    }

    public void getEbookId(){
        Intent intent = getIntent();
        if(intent.hasExtra(Constants.SP_KEY_BOOK_ID)) {
            ebookId = intent.getLongExtra(Constants.SP_KEY_BOOK_ID,Integer.MAX_VALUE);
            bundle.putLong(Constants.SP_KEY_BOOK_ID, ebookId);
        }
    }

    private void initTitleBar() {
        TitleBarModel titleBarModel = new TitleBarModel(getEventBus());
        titleBarModel.title.set(ResManager.getString(R.string.setting_name));
        titleBarModel.backEvent.set(new FinishSettingEvent());
        binding.titleBar.setTitleModel(titleBarModel);
    }

    private void initFragment() {
        if (fragmentStack == null) {
            fragmentStack = new Stack<>();
        }
    }

    private void initSystemBar() {
        systemBarModel = MainBundle.getInstance().getSystemBarModel();
        binding.settingSystemBar.setSystemBarModel(systemBarModel);
        systemBarModel.registerReceiver(JDReadApplication.getInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getEventBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getEventBus().unregister(this);
    }

    public EventBus getEventBus() {
        return SettingBundle.getInstance().getEventBus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSystemBarClickedEvent(SystemBarClickedEvent event) {
        if (systemBarPopupWindowModel == null) {
            systemBarPopupWindowModel = new SystemBarPopupWindow.SystemBarPopupModel();
        } else {
            systemBarPopupWindowModel.brightnessModel.updateLight();
            systemBarPopupWindowModel.updateRefreshMode();
        }
        SystemBarPopupWindow systemBarPopupWindow = new SystemBarPopupWindow(this, systemBarPopupWindowModel);
        systemBarPopupWindow.show(binding.settingSystemBar.getRoot());
    }

    private BaseFragment.ChildViewEventCallBack childViewEventCallBack = new BaseFragment.ChildViewEventCallBack() {
        @Override
        public void gotoView(String childClassName) {
            getEventBus().post(new PushChildViewToStackEvent(childClassName, null));
        }

        @Override
        public void gotoView(String childClassName, Bundle bundle) {
            getEventBus().post(new PushChildViewToStackEvent(childClassName, bundle));
        }

        @Override
        public void viewBack() {
            PopCurrentChildViewEvent event = new PopCurrentChildViewEvent();
            getEventBus().post(event);
        }

        @Override
        public void hideOrShowSystemBar(boolean flags) {

        }

        @Override
        public void hideOrShowFunctionBar(boolean flags) {

        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushChildViewToStackEvent(PushChildViewToStackEvent event) {
        pushChildViewToStack(event.childClassName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopCurrentChildViewEvent(PopCurrentChildViewEvent event) {
        String childClassName = popChildView();
        isShowTitle();
        switchCurrentFragment(getPageView(childClassName));
    }

    public String popChildView() {
        if (fragmentStack.size() <= 1) {
            return fragmentStack.peek();
        }
        fragmentStack.pop();
        return fragmentStack.peek();
    }

    private void isShowTitle() {
        if(showTitle) {
            binding.titleBar.getRoot().setVisibility(fragmentStack.size() <= 1 ? View.VISIBLE : View.GONE);
        }
    }

    private void pushChildViewToStack(String childClassName) {
        switchCurrentFragment(getPageView(childClassName));
        fragmentStack.push(childClassName);
        isShowTitle();
    }

    private void switchCurrentFragment(@NonNull BaseFragment baseFragment) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        baseFragment.setBundle(bundle);
        currentFragment = baseFragment;
        fragmentManager.beginTransaction().replace(R.id.setting_content_view, baseFragment).commitNowAllowingStateLoss();
    }

    private BaseFragment getPageView(String childViewName) {
        BaseFragment baseFragment = childViewList.get(childViewName);
        if (baseFragment == null) {
            try {
                Class clazz = Class.forName(childViewName);
                baseFragment = (BaseFragment) clazz.newInstance();
                childViewList.put(childViewName, baseFragment);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        setChildViewInfo(baseFragment);
        return baseFragment;
    }

    private void setChildViewInfo(BaseFragment baseFragment) {
        baseFragment.setViewEventCallBack(childViewEventCallBack);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTimeFormatEvent(UpdateTimeFormatEvent event) {
        binding.settingSystemBar.onyxDigitalClock.setFormat();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishSettingEvent(FinishSettingEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookDetailTopBackEvent(TopBackEvent event) {
        if(currentFragment != null && currentFragment instanceof BookDetailFragment) {
            finish();
        }
    }
}
