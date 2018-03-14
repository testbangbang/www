package com.onyx.jdread.main.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityMainBinding;
import com.onyx.jdread.library.event.BackToRootFragment;
import com.onyx.jdread.library.model.LibraryDataBundle;
import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.main.action.ChangeFunctionBarAction;
import com.onyx.jdread.main.action.InitMainViewFunctionBarAction;
import com.onyx.jdread.main.adapter.FunctionBarAdapter;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ResManager;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.event.KeyCodeEnterEvent;
import com.onyx.jdread.main.event.NetworkConnectedEvent;
import com.onyx.jdread.main.event.PopCurrentChildViewEvent;
import com.onyx.jdread.main.event.PushChildViewToStackEvent;
import com.onyx.jdread.main.event.ShowBackTabEvent;
import com.onyx.jdread.main.event.SystemBarBackToSettingEvent;
import com.onyx.jdread.main.event.SystemBarClickedEvent;
import com.onyx.jdread.main.event.TabLongClickedEvent;
import com.onyx.jdread.main.event.UpdateTimeFormatEvent;
import com.onyx.jdread.main.event.UsbDisconnectedEvent;
import com.onyx.jdread.main.event.WifiStateChangeEvent;
import com.onyx.jdread.main.model.FragmentBarModel;
import com.onyx.jdread.main.model.FunctionBarItem;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.main.model.MainBundle;
import com.onyx.jdread.main.model.MainViewModel;
import com.onyx.jdread.main.model.SystemBarModel;
import com.onyx.jdread.main.receiver.ScreenStateReceive;
import com.onyx.jdread.main.view.SystemBarPopupWindow;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.HideSoftWindowEvent;
import com.onyx.jdread.personal.event.PersonalErrorEvent;
import com.onyx.jdread.personal.event.RequestFailedEvent;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalViewModel;
import com.onyx.jdread.personal.model.UserLoginViewModel;
import com.onyx.jdread.personal.ui.PersonalFragment;
import com.onyx.jdread.setting.ui.SettingFragment;
import com.onyx.jdread.setting.ui.SystemUpdateFragment;
import com.onyx.jdread.shop.ui.NetWorkErrorFragment;
import com.onyx.jdread.util.Utils;

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
    private FunctionBarModel functionBarModel;
    private FunctionBarAdapter functionBarAdapter;
    private SystemBarModel systemBarModel;
    private SystemBarPopupWindow.SystemBarPopupModel systemBarPopupWindowModel;
    private ScreenStateReceive screenStateReceive;
    private int tabCheckedCount = 0;
    private boolean inSystemBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            }
        }
        initView();
        initLibrary();
        initData();
    }

    private void initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    }

    private void initLibrary() {
        registerScreenReceive();
    }

    private void initData() {
        JDReadApplication.getInstance().automaticLogin();
        binding.setMainViewModel(new MainViewModel());
        initSystemBar();
        initFunctionBar();
        initFirstChildView();
    }

    private void initFirstChildView() {
        String childClassName = LibraryFragment.class.getName();
        if (ViewConfig.isCheckFragment(childClassName)) {
            changeFunctionItem(childClassName);
        }
        FragmentBarModel barModel = popCurrentChildView(functionBarModel.findFunctionGroup());
        switchCurrentFragment(barModel.getBaseFragment(), null);
    }

    private void initSystemBar() {
        systemBarModel = MainBundle.getInstance().getSystemBarModel();
        binding.mainSystemBar.setSystemBarModel(systemBarModel);
        systemBarModel.registerReceiver(JDReadApplication.getInstance());
    }

    private void initFunctionBar() {
        functionBarModel = new FunctionBarModel();
        binding.mainFunctionBar.setFunctionBarModel(functionBarModel);
        PageRecyclerView functionBarRecycler = getFunctionBarRecycler();
        functionBarRecycler.setLayoutManager(new DisableScrollGridManager(getApplicationContext()));
        functionBarAdapter = new FunctionBarAdapter();
        setFunctionAdapter(functionBarRecycler);
    }

    private void setFunctionAdapter(PageRecyclerView functionBarRecycler) {
        if (functionBarRecycler == null) {
            return;
        }
        boolean show = PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, false);
        PreferenceManager.setBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, show);
        int col = getResources().getInteger(R.integer.function_bar_col);
        functionBarAdapter.setRowAndCol(functionBarAdapter.getRowCount(), show ? col : col - 1);
        functionBarRecycler.setAdapter(functionBarAdapter);
        updateFunctionBar();
    }

    private void updateFunctionBar() {
        if (functionBarModel.itemModels.size() == 0) {
            initFunctionBarAction();
        } else {
            changeFunctionBarAction();
        }
    }

    private void changeFunctionBarAction() {
        ChangeFunctionBarAction changeFunctionBarAction = new ChangeFunctionBarAction(functionBarModel);
        changeFunctionBarAction.execute(MainBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateFunctionBarView();
            }
        });
    }

    private void initFunctionBarAction() {
        InitMainViewFunctionBarAction initFunctionBarAction = new InitMainViewFunctionBarAction(functionBarModel);
        initFunctionBarAction.execute(MainBundle.getInstance(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                updateFunctionBarView();
            }
        });
    }

    private void isShowBackTab(boolean show) {
        PreferenceManager.setBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, show);
        setFunctionAdapter(getFunctionBarRecycler());
    }

    private void updateFunctionBarView() {
        PageRecyclerView barRecycler = getFunctionBarRecycler();
        if (barRecycler == null) {
            return;
        }
        barRecycler.getAdapter().notifyDataSetChanged();
    }

    private PageRecyclerView getFunctionBarRecycler() {
        if (binding == null) {
            return null;
        }
        return binding.mainFunctionBar.functionBarRecycler;
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

    @Override
    protected void onDestroy() {
        systemBarModel.unRegisterReceiver(JDReadApplication.getInstance());
        unregisterReceiver(screenStateReceive);
        super.onDestroy();
    }

    private void registerScreenReceive() {
        screenStateReceive = new ScreenStateReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ScreenStateReceive.SCREEN_ON);
        intentFilter.addAction(ScreenStateReceive.SCREEN_OFF);
        registerReceiver(screenStateReceive, intentFilter);
    }

    public void switchCurrentFragment(@NonNull BaseFragment baseFragment, @Nullable Bundle bundle) {
        if (StringUtils.isNotBlank(currentChildViewName) && currentChildViewName.equals(
                baseFragment.getClass().getName())) {
            return;
        }
        if (StringUtils.isNotBlank(currentChildViewName) && currentChildViewName.equals(
                baseFragment.getClass().getName())) {
            return;
        }
        initFragmentManager();
        notifyChildViewChangeWindow();
        baseFragment.setBundle(bundle);
        transaction.replace(R.id.main_content_view, baseFragment).commitNowAllowingStateLoss();
        processEpdGcOnce();
        saveChildViewInfo(baseFragment);
    }

    private void processEpdGcOnce() {
        if (tabCheckedCount >= ResManager.getInteger(R.integer.refresh_count)) {
            EpdController.appliGcOnce();
            tabCheckedCount = 0;
        } else {
            tabCheckedCount++;
        }
    }

    private void saveChildViewInfo(@NonNull BaseFragment baseFragment) {
        addToChildViewList(baseFragment);
        currentFragment = baseFragment;
        currentChildViewName = baseFragment.getClass().getName();
    }

    private void addToChildViewList(@NonNull BaseFragment baseFragment) {
        if (baseFragment.getClass().getName().equals(SystemUpdateFragment.class.getName())) {
            childViewList.put(baseFragment.getClass().getName(), baseFragment);
        }
    }

    private Bundle getCurrentBundle() {
        if (currentFragment == null) {
            return null;
        }
        return currentFragment.getBundle();
    }

    private void changeFunctionItem(String childViewName) {
        ViewConfig.FunctionModule functionModule = ViewConfig.findChildViewParentId(childViewName);
        functionBarModel.changeTabSelection(functionModule);
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

    private BaseFragment getPageView(String childViewName) {
        BaseFragment baseFragment = childViewList.get(childViewName);
        if (baseFragment == null) {
            try {
                Class clazz = Class.forName(childViewName);
                baseFragment = (BaseFragment) clazz.newInstance();
                baseFragment.setViewEventCallBack(childViewEventCallBack);
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
            EventBus.getDefault().post(new PushChildViewToStackEvent(childClassName, getCurrentBundle()));
        }

        @Override
        public void gotoView(String childClassName, Bundle bundle) {
            EventBus.getDefault().post(new PushChildViewToStackEvent(childClassName, bundle));
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
            functionBarModel.setIsShow(flags);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this, true);
        setFunctionAdapter(getFunctionBarRecycler());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            LibraryDataBundle.getInstance().getEventBus().post(new KeyCodeEnterEvent());
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            inSystemBar = event.getY() < binding.mainSystemBar.getRoot().getHeight();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE && inSystemBar) {
            event.setAction(MotionEvent.ACTION_UP);
        }

        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }

    private FragmentBarModel createFragmentBarModel(String childClassName) {
        FragmentBarModel model = new FragmentBarModel();
        model.name.set(childClassName);
        model.baseFragment.set(getPageView(childClassName));
        return model;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushChildViewToStackEvent(PushChildViewToStackEvent event) {
        pushChildViewToStack(event.childClassName, event.bundle);
    }

    private void pushChildViewToStack(String childClassName, Bundle bundle) {
        FragmentBarModel model = createFragmentBarModel(childClassName);
        switchCurrentFragment(model.getBaseFragment(), bundle);
        if (ViewConfig.isCheckFragment(childClassName)) {
            changeFunctionItem(childClassName);
        }
        FunctionBarItem functionBarItem = functionBarModel.findFunctionGroup();
        if (functionBarItem != null) {
            functionBarItem.getStackList().push(model);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopCurrentChildViewEvent(PopCurrentChildViewEvent event) {
        FunctionBarItem functionBarItem = functionBarModel.findFunctionGroup();
        if (functionBarItem != null) {
            FragmentBarModel barModel = popCurrentChildView(functionBarItem);
            if (isNetWorkFragment(currentFragment.getClass().getName()) || isNetWorkFragment(barModel.getName())) {
                barModel = functionBarItem.getStackList().popChildView();
            }
            switchCurrentFragment(barModel.getBaseFragment(), barModel.getBaseFragment().getBundle());
        }
        if (currentFragment != null && currentFragment.getClass().getName().equals(LibraryFragment.class.getName())) {
            LibraryDataBundle.getInstance().getEventBus().post(new BackToRootFragment());
        }
    }

    private FragmentBarModel popCurrentChildView(FunctionBarItem functionBarItem) {
        FragmentBarModel barModel = functionBarItem.getStackList().popChildView();
        if (barModel.getBaseFragment() == null) {
            barModel.setBaseFragment(getPageView(barModel.getName()));
        }
        return barModel;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFunctionBarTabModel(FunctionBarItem event) {
        boolean isSelectedBefore = event.equals(functionBarModel.getSelectedFunctionItem());
        functionBarModel.changeTabSelection(event.functionModule.get());
        if (isSelectedBefore && currentFragment != null && currentFragment.getClass().getName().equals(LibraryFragment.class.getName())) {
            LibraryDataBundle.getInstance().getEventBus().post(new BackToRootFragment());
        }
        if (isNetWorkFragment(event.getStackList().peek().getName())) {
            event.getStackList().pop();
        }
        FragmentBarModel model = isSelectedBefore ? event.getStackList().remainLastStack() :
                event.getStackList().peek();
        if (model.getBaseFragment() == null) {
            model.setBaseFragment(getPageView(model.getName()));
        }
        switchCurrentFragment(model.getBaseFragment(), model.getBaseFragment().getBundle());
    }

    private boolean isNetWorkFragment(String peek) {
        return NetWorkErrorFragment.class.getName().equals(peek);
    }

    @Subscribe
    public void onUsbDisconnectedEvent(UsbDisconnectedEvent event) {
        JDReadApplication.getInstance().dealWithMtpBuffer();
    }

    @Subscribe
    public void onShowBackTabEvent(ShowBackTabEvent event) {
        isShowBackTab(event.isShow());
    }

    @Subscribe
    public void onSystemBarClickedEvent(SystemBarClickedEvent event) {
        if (systemBarPopupWindowModel == null) {
            systemBarPopupWindowModel = new SystemBarPopupWindow.SystemBarPopupModel();
        } else {
            systemBarPopupWindowModel.brightnessModel.updateLight();
            systemBarPopupWindowModel.updateRefreshMode();
        }
        SystemBarPopupWindow systemBarPopupWindow = new SystemBarPopupWindow(this, systemBarPopupWindowModel);
        systemBarPopupWindow.show(binding.mainSystemBar.getRoot());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginResultEvent(UserLoginResultEvent event) {
        if (getResources().getString(R.string.login_success).equals(event.getMessage())) {
            JDReadApplication.getInstance().setLogin(true);
            clearInput();
            LoginHelper.dismissUserLoginDialog();
            if (StringUtils.isNotBlank(event.getTargetView())) {
                childViewEventCallBack.gotoView(event.getTargetView());
            }
            if (PersonalFragment.class.getName().equals(currentChildViewName)) {
                PersonalFragment fragment = (PersonalFragment) currentFragment;
                fragment.setRefresh();
            }

        } else {
            ToastUtil.showToast(this, event.getMessage());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkConnectedEvent(NetworkConnectedEvent event) {
        JDReadApplication.getInstance().automaticLogin();
        SystemUpdateFragment fragment = (SystemUpdateFragment) childViewList.get(SystemUpdateFragment.class.getName());
        if (fragment != null) {
            fragment.keepDownload();
        }
    }

    @Subscribe
    public void onSystemBarBackToSettingEvent(SystemBarBackToSettingEvent event) {
        childViewEventCallBack.gotoView(SettingFragment.class.getName());
    }

    @Subscribe
    public void onWifiStateChangeEvent(WifiStateChangeEvent event) {
        if (systemBarPopupWindowModel != null) {
            systemBarPopupWindowModel.updateWifi();
        }
    }

    private void clearInput() {
        getUserLoginViewModel().cleanInput();
    }

    public UserLoginViewModel getUserLoginViewModel() {
        return getPersonalViewModel().getUserLoginViewModel();
    }

    public PersonalViewModel getPersonalViewModel() {
        return PersonalDataBundle.getInstance().getPersonalViewModel();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPersonalErrorEvent(PersonalErrorEvent event) {
        String[] errors = PersonalErrorEvent.getThrowableStringRep(event.throwable);
        PersonalErrorEvent.printThrowable(errors);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestFailedEvent(RequestFailedEvent event) {
        ToastUtil.showToast(event.getMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabLongClickedEvent(TabLongClickedEvent event) {
        if (ViewConfig.FunctionModule.isBackModule(event.functionItem.getFunctionModule())) {
            EventBus.getDefault().post(new ShowBackTabEvent(false));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHideSoftWindowEvent(HideSoftWindowEvent event) {
        Utils.hideSoftWindow(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateTimeFormatEvent(UpdateTimeFormatEvent event) {
        binding.mainSystemBar.onyxDigitalClock.setFormat();
    }
}
