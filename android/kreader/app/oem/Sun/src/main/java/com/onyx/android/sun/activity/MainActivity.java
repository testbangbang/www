package com.onyx.android.sun.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.databinding.ViewDataBinding;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sun.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.SunApplication;
import com.onyx.android.sun.bean.MainTabBean;
import com.onyx.android.sun.bean.User;
import com.onyx.android.sun.common.AppConfigData;
import com.onyx.android.sun.common.CommonNotices;
import com.onyx.android.sun.common.Constants;
import com.onyx.android.sun.databinding.ActivityMainBinding;
import com.onyx.android.sun.event.ApkDownloadSucceedEvent;
import com.onyx.android.sun.event.BackToHomeworkFragmentEvent;
import com.onyx.android.sun.event.HaveNewVersionApkEvent;
import com.onyx.android.sun.event.HaveNewVersionEvent;
import com.onyx.android.sun.event.StartDownloadingEvent;
import com.onyx.android.sun.event.ToChangePasswordEvent;
import com.onyx.android.sun.event.ToCorrectEvent;
import com.onyx.android.sun.event.ToHomeworkEvent;
import com.onyx.android.sun.event.ToMainFragmentEvent;
import com.onyx.android.sun.event.ToRankingEvent;
import com.onyx.android.sun.event.ToUserCenterEvent;
import com.onyx.android.sun.event.UnfinishedEvent;
import com.onyx.android.sun.event.UpdateDownloadSucceedEvent;
import com.onyx.android.sun.fragment.BaseFragment;
import com.onyx.android.sun.fragment.ChangePasswordFragment;
import com.onyx.android.sun.fragment.ChildViewID;
import com.onyx.android.sun.fragment.CorrectFragment;
import com.onyx.android.sun.fragment.DeviceSettingFragment;
import com.onyx.android.sun.fragment.FillHomeworkFragment;
import com.onyx.android.sun.fragment.HomeWorkFragment;
import com.onyx.android.sun.fragment.MainFragment;
import com.onyx.android.sun.fragment.RankingFragment;
import com.onyx.android.sun.fragment.UserCenterFragment;
import com.onyx.android.sun.interfaces.MainView;
import com.onyx.android.sun.presenter.MainPresenter;
import com.onyx.android.sun.utils.ApkUtils;
import com.onyx.android.sun.utils.SystemUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements MainView, View.OnClickListener {
    private ActivityMainBinding mainBinding;
    private User user = new User();
    private FragmentManager fragmentManager;
    private int currentPageID = ChildViewID.BASE_VIEW;
    private FragmentTransaction transaction;
    private BaseFragment currentFragment;
    private Map<Integer, BaseFragment> childViewList = new HashMap<>();
    private String mainFragmentTitle = "";
    private String userCenterFragmentTitle = "";
    private String changePasswordFragmentTitle = "";
    private String currentTitle = "";
    private int userCenterTitleIconID = R.drawable.icon_user_center;
    private int backTitleIconID = R.drawable.icon_back_white;
    private int currentTitleIconID = 0;
    private boolean isShowTabLayoutAndNewMessageView = true;

    @Override
    protected void initData() {
        restoreUserName();
        currentTitleIconID = userCenterTitleIconID;
        userCenterFragmentTitle = getString(R.string.user_center_title);
        changePasswordFragmentTitle = getString(R.string.user_center_fragment_change_password);
    }

    private void restoreUserName() {
        String name = PreferenceManager.getStringValue(SunApplication.getInstance(),Constants.SP_KEY_USER_NAME,"");
        if (!TextUtils.isEmpty(name)){
            mainFragmentTitle = name + getString(R.string.main_activity_hello);
            currentTitle = mainFragmentTitle;
        }
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        mainBinding = (ActivityMainBinding) binding;
        mainBinding.setVariable(BR.presenter, new MainPresenter());
        mainBinding.setVariable(BR.user, user);
        mainBinding.setIsShowTabLayoutAndNewMessageView(true);
        mainBinding.setTitle(mainFragmentTitle);
        mainBinding.setListener(this);

        List<MainTabBean> mainTabList = AppConfigData.getMainTabList();
        for (MainTabBean mainTabBean : mainTabList) {
            TabLayout.Tab tab = mainBinding.mainActivityTab.newTab();
            tab.setText(mainTabBean.tabName);
            tab.setTag(mainTabBean.tag);
            mainBinding.mainActivityTab.addTab(tab);
        }
    }

    @Override
    protected void initListener() {
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN);
        mainBinding.mainActivityTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchCurrentFragment((int) tab.getTag());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected int getViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news_image:
                switchCurrentFragment(ChildViewID.FRAGMENT_DEVICE_SETTING);
                break;
            case R.id.ll_main_activity_title_container:
                onClickTitleContainer();
                break;
        }
    }

    private void onClickTitleContainer() {
        switch (currentPageID){
            case ChildViewID.FRAGMENT_USER_CENTER:
                switchCurrentFragment(ChildViewID.FRAGMENT_MAIN);
                break;
            case ChildViewID.FRAGMENT_CHANGE_PASSWORD:
                switchCurrentFragment(ChildViewID.FRAGMENT_USER_CENTER);
                break;
            default:
                switchCurrentFragment(ChildViewID.FRAGMENT_USER_CENTER);
                break;
        }
        setTitleAndIcon();
    }

    private void setTitleAndIcon() {
        switch (currentPageID){
            case ChildViewID.FRAGMENT_CHANGE_PASSWORD:
                isShowTabLayoutAndNewMessageView = false;
                currentTitleIconID = backTitleIconID;
                currentTitle = changePasswordFragmentTitle;
                break;
            case ChildViewID.FRAGMENT_USER_CENTER:
                isShowTabLayoutAndNewMessageView = false;
                currentTitleIconID = backTitleIconID;
                currentTitle = userCenterFragmentTitle;
                break;
            default:
                isShowTabLayoutAndNewMessageView = true;
                currentTitleIconID = userCenterTitleIconID;
                currentTitle = mainFragmentTitle;
                break;
        }
        mainBinding.setIsShowTabLayoutAndNewMessageView(isShowTabLayoutAndNewMessageView);
        mainBinding.ivMainActivityTitleIcon.setImageResource(currentTitleIconID);
        mainBinding.setTitle(currentTitle);
    }

    public void switchCurrentFragment(int pageID) {
        if (currentPageID == pageID) {
            return;
        }
        if (fragmentManager == null) {
            fragmentManager = getFragmentManager();
        }
        transaction = fragmentManager.beginTransaction();

        if (currentFragment != null && currentFragment.isVisible()) {
            transaction.hide(currentFragment);
        }
        BaseFragment baseFragment = getPageView(pageID);
        if (baseFragment.isStored) {
            transaction.show(baseFragment);
        } else {
            transaction.add(R.id.main_frame_layout, baseFragment);
            childViewList.put(pageID, baseFragment);
        }
        currentFragment = baseFragment;
        currentPageID = pageID;
        transaction.commitAllowingStateLoss();
    }

    private BaseFragment getPageView(int pageID) {
        BaseFragment baseFragment = childViewList.get(pageID);
        if (baseFragment == null) {
            switch (pageID) {
                case ChildViewID.FRAGMENT_MAIN:
                    baseFragment = new MainFragment();
                    break;
                case ChildViewID.FRAGMENT_EXAMINATION_WORK:
                    baseFragment = new HomeWorkFragment();
                    break;
                case ChildViewID.FRAGMENT_GOAL_ADVANCED:
                    baseFragment = new MainFragment();
                    break;
                case ChildViewID.FRAGMENT_STUDY_MANAGEMENT:
                    baseFragment = new MainFragment();
                    break;
                case ChildViewID.FRAGMENT_FILL_HOMEWORK:
                    baseFragment = new FillHomeworkFragment();
                    break;
                case ChildViewID.FRAGMENT_CORRECT:
                    baseFragment = new CorrectFragment();
                    break;
                case ChildViewID.FRAGMENT_RANKING:
                    baseFragment = new RankingFragment();
                    break;
                case ChildViewID.FRAGMENT_DEVICE_SETTING:
                    baseFragment = new DeviceSettingFragment();
                    break;
                case ChildViewID.FRAGMENT_USER_CENTER:
                    baseFragment = new UserCenterFragment();
                    break;
                case ChildViewID.FRAGMENT_CHANGE_PASSWORD:
                    baseFragment = new ChangePasswordFragment();
                    break;
            }
        } else {
            baseFragment.isStored = true;
        }
        return baseFragment;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        BaseFragment currentFragment = getPageView(currentPageID);
        if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (currentFragment != null && currentFragment.onKeyBack()) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToHomeworkEvent(ToHomeworkEvent event) {
        mainBinding.mainActivityTab.getTabAt(ChildViewID.FRAGMENT_EXAMINATION_WORK).select();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnfinishedEvent(UnfinishedEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_FILL_HOMEWORK);
        FillHomeworkFragment fillHomeworkFragment = (FillHomeworkFragment) getPageView(ChildViewID.FRAGMENT_FILL_HOMEWORK);
        fillHomeworkFragment.setTaskId(event.getId(), event.getType(), event.getTitle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToHomeworkFragmentEvent(BackToHomeworkFragmentEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_EXAMINATION_WORK);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToCorrectEvent(ToCorrectEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_CORRECT);
        CorrectFragment correctFragment = (CorrectFragment) getPageView(ChildViewID.FRAGMENT_CORRECT);
        correctFragment.setStartTimer(event.isHasCorrected());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToRankingEvent(ToRankingEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_RANKING);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToMainFragmentEvent(ToMainFragmentEvent event) {
        mainBinding.mainActivityTab.getTabAt(ChildViewID.FRAGMENT_MAIN).select();
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN);
        setTitleAndIcon();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToChangePasswordEvent(ToChangePasswordEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_CHANGE_PASSWORD);
        setTitleAndIcon();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToUserCenterEventEvent(ToUserCenterEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_USER_CENTER);
        setTitleAndIcon();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHaveNewVersionApkEvent(HaveNewVersionApkEvent event) {
        ApplicationUpdate applicationUpdate = event.getApplicationUpdate();
        Map<String, List<String>> changeLogs = applicationUpdate.changeLogs;
        String[] downloadUrlList = applicationUpdate.downloadUrlList;
        String language = getResources().getConfiguration().locale.toString();
        String message = String.format(getString(R.string.current_version), SystemUtils.getAPPVersionCode(this)) + "--->";
        message += String.format(getString(R.string.update_version), applicationUpdate.versionCode) + "\n";
        message += getString(R.string.update_content);
        if (changeLogs != null && changeLogs.size() > 0) {
            List<String> messageList = changeLogs.get(language);
            for (int i = 0; i < messageList.size(); i++) {
                message += messageList.get(i);
                message += "\n";
            }
        }
        if (downloadUrlList == null || downloadUrlList.length <= 0) {
            CommonNotices.show(getString(R.string.without_new_version));
            return;
        }
        ApkUtils.showNewApkDialog(SunApplication.getInstance(), message, downloadUrlList[0]);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHaveNewVersionEvent(HaveNewVersionEvent event) {
        FirmwareUpdateRequest request = event.getRequest();
        Firmware resultFirmware = request.getResultFirmware();
        String changeLog = resultFirmware.getChangeLog();
        if (StringUtils.isNullOrEmpty(changeLog)) {
            changeLog = resultFirmware.buildDisplayId;
        }
        String downloadUrl = resultFirmware.getUrl();
        if (StringUtils.isNotBlank(downloadUrl)) {
            ApkUtils.showLocalCheckDialog(this, changeLog, downloadUrl);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onApkDownloadSucceedEvent(ApkDownloadSucceedEvent event) {
        ApkUtils.installApk(SunApplication.getInstance(), Constants.APK_DOWNLOAD_PATH);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdateDownloadSucceedEvent(UpdateDownloadSucceedEvent event) {
        ApkUtils.firmwareLocal();
        dismissAllProgressDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStartDownloadingEvent(StartDownloadingEvent event) {
        showProgressDialog(event, R.string.downloading, null);
    }
}
