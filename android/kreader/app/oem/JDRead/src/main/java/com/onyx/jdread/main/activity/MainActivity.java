package com.onyx.jdread.main.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.ui.view.DisableScrollGridManager;
import com.onyx.android.sdk.ui.view.PageRecyclerView;
import com.onyx.android.sdk.utils.DeviceUtils;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.jdread.JDReadApplication;
import com.onyx.jdread.R;
import com.onyx.jdread.databinding.ActivityMainBinding;
import com.onyx.jdread.library.ui.LibraryFragment;
import com.onyx.jdread.library.action.RxFileSystemScanAction;
import com.onyx.jdread.main.action.InitMainViewFunctionBarAction;
import com.onyx.jdread.main.adapter.FunctionBarAdapter;
import com.onyx.jdread.main.common.BaseFragment;
import com.onyx.jdread.main.common.ToastUtil;
import com.onyx.jdread.main.common.ViewConfig;
import com.onyx.jdread.main.event.ChangeChildViewEvent;
import com.onyx.jdread.main.event.ModifyLibraryDataEvent;
import com.onyx.jdread.main.event.PopCurrentChildViewEvent;
import com.onyx.jdread.main.event.PushChildViewToStackEvent;
import com.onyx.jdread.main.event.ShowBackTabEvent;
import com.onyx.jdread.main.event.UsbDisconnectedEvent;
import com.onyx.jdread.main.model.FunctionBarItem;
import com.onyx.jdread.main.model.FunctionBarModel;
import com.onyx.jdread.main.model.MainViewModel;
import com.onyx.jdread.main.model.SystemBarModel;
import com.onyx.jdread.personal.common.LoginHelper;
import com.onyx.jdread.personal.event.UserLoginResultEvent;
import com.onyx.jdread.personal.model.PersonalDataBundle;
import com.onyx.jdread.personal.model.PersonalViewModel;
import com.onyx.jdread.personal.model.UserLoginViewModel;
import com.onyx.jdread.personal.ui.LoginFragment;
import com.onyx.jdread.personal.ui.PersonalFragment;
import com.onyx.jdread.setting.request.RxLoadPicByPathRequest;
import com.onyx.jdread.shop.ui.ShopCartFragment;
import com.onyx.jdread.shop.ui.ShopFragment;

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
        EventBus.getDefault().register(this);
    }

    private void initData() {
        binding.setMainViewModel(new MainViewModel());
        initSystemBar();
        initFunctionBar();
        switchCurrentFragment(LibraryFragment.class.getName());
    }

    private void initSystemBar() {
        binding.mainSystemBar.setSystemBarModel(new SystemBarModel());
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
        boolean show = PreferenceManager.getBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, false);
        PreferenceManager.setBooleanValue(JDReadApplication.getInstance(), R.string.show_back_tab_key, show);
        int col = getResources().getInteger(R.integer.function_bar_col);
        functionBarAdapter.setRowAndCol(functionBarAdapter.getRowCount(), show ? col : col - 1);
        functionBarRecycler.setAdapter(functionBarAdapter);
        updateFunctionBar();
    }

    private void updateFunctionBar() {
        InitMainViewFunctionBarAction initFunctionBarAction = new InitMainViewFunctionBarAction(functionBarModel);
        initFunctionBarAction.execute(JDReadApplication.getDataBundle(), new RxCallback() {
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
        return binding.mainFunctionBar.functionBarRecycler;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void switchCurrentFragment(String childViewName) {
        if (StringUtils.isNullOrEmpty(childViewName)) {
            return;
        }
        if (StringUtils.isNotBlank(currentChildViewName) && currentChildViewName.equals(childViewName)) {
            return;
        }
        initFragmentManager();
        notifyChildViewChangeWindow();
        BaseFragment baseFragment = getPageView(childViewName);

        transaction.replace(R.id.main_content_view, new ShopCartFragment());
        transaction.commitAllowingStateLoss();
        changeFunctionItem(childViewName);
        saveChildViewInfo(childViewName, baseFragment);
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
            functionBarModel.setIsShow(flags);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        DeviceUtils.setFullScreenOnResume(this, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (currentFragment instanceof ShopFragment) {
            GestureDetector gestureDetector = ((ShopFragment) currentFragment).getGestureDetector();
            gestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPushChildViewToStackEvent(PushChildViewToStackEvent event) {
        switchCurrentFragment(event.childClassName);
        ViewConfig.FunctionModule functionModule = ViewConfig.findChildViewParentId(event.childClassName);
        FunctionBarItem functionBarItem = functionBarModel.findFunctionGroup(functionModule);
        if (functionBarItem != null) {
            functionBarItem.getStackList().push(event.childClassName);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPopCurrentChildViewEvent(PopCurrentChildViewEvent event) {
        ViewConfig.FunctionModule functionModule = ViewConfig.findChildViewParentId(currentChildViewName);
        FunctionBarItem functionBarItem = functionBarModel.findFunctionGroup(functionModule);
        if (functionBarItem != null) {
            String childClassName = functionBarItem.getStackList().popChildView();
            switchCurrentFragment(childClassName);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeChildViewEvent(ChangeChildViewEvent event) {
        String name = null;
        if (event.childViewName.equals(PersonalFragment.class.getName())) {
            if (JDReadApplication.getInstance().getLogin()) {
                name = event.childViewName;
            } else {
                name = LoginFragment.class.getName();
            }
        } else {
            name = event.childViewName;
        }
        switchCurrentFragment(name);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFunctionBarTabModel(FunctionBarItem event) {
        functionBarModel.changeTabSelection(event.functionModule.get());
    }

    @Subscribe
    public void onUsbDisconnectedEvent(UsbDisconnectedEvent event) {
        JDReadApplication.getInstance().dealWithMtpBuffer();
        RxFileSystemScanAction scanAction = new RxFileSystemScanAction(RxFileSystemScanAction.MMC_STORAGE_ID, true);
        scanAction.execute(JDReadApplication.getDataBundle(), new RxCallback() {
            @Override
            public void onNext(Object o) {
                JDReadApplication.getDataBundle().getEventBus().post(new ModifyLibraryDataEvent());
            }
        });
    }

    @Subscribe
    public void onUsbDisconnectedEvent(ShowBackTabEvent event) {
        isShowBackTab(event.isShow());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginResultEvent(UserLoginResultEvent event) {
        ToastUtil.showToast(this, event.getMessage());
        if (getResources().getString(R.string.login_success).equals(event.getMessage())) {
            JDReadApplication.getInstance().setLogin(true);
            clearInput();
            LoginHelper.dismissUserLoginDialog();
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
}
