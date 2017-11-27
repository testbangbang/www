package com.onyx.android.plato.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.databinding.ViewDataBinding;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.onyx.android.plato.BR;
import com.onyx.android.plato.R;
import com.onyx.android.plato.SunApplication;
import com.onyx.android.plato.bean.MainTabBean;
import com.onyx.android.plato.bean.User;
import com.onyx.android.plato.cloud.bean.ContentBean;
import com.onyx.android.plato.cloud.bean.QuestionViewBean;
import com.onyx.android.plato.common.AppConfigData;
import com.onyx.android.plato.common.CommonNotices;
import com.onyx.android.plato.common.Constants;
import com.onyx.android.plato.databinding.ActivityMainBinding;
import com.onyx.android.plato.event.ApkDownloadSucceedEvent;
import com.onyx.android.plato.event.BackToHomeworkFragmentEvent;
import com.onyx.android.plato.event.DeleteRemindEvent;
import com.onyx.android.plato.event.EmptyEvent;
import com.onyx.android.plato.event.HaveNewVersionApkEvent;
import com.onyx.android.plato.event.HaveNewVersionEvent;
import com.onyx.android.plato.event.HomeworkFinishedEvent;
import com.onyx.android.plato.event.HomeworkReportEvent;
import com.onyx.android.plato.event.HomeworkUnfinishedEvent;
import com.onyx.android.plato.event.OnBackPressEvent;
import com.onyx.android.plato.event.ParseAnswerEvent;
import com.onyx.android.plato.event.RefreshFragmentEvent;
import com.onyx.android.plato.event.StartDownloadingEvent;
import com.onyx.android.plato.event.ToChangePasswordEvent;
import com.onyx.android.plato.event.ToCorrectEvent;
import com.onyx.android.plato.event.ToHomeworkEvent;
import com.onyx.android.plato.event.ToMainFragmentEvent;
import com.onyx.android.plato.event.ToRankingEvent;
import com.onyx.android.plato.event.ToStudyReportDeatilEvent;
import com.onyx.android.plato.event.ToUserCenterEvent;
import com.onyx.android.plato.event.UnfinishedEvent;
import com.onyx.android.plato.event.UpdateDownloadSucceedEvent;
import com.onyx.android.plato.fragment.BaseFragment;
import com.onyx.android.plato.fragment.ChangePasswordFragment;
import com.onyx.android.plato.fragment.ChildViewID;
import com.onyx.android.plato.fragment.CorrectFragment;
import com.onyx.android.plato.fragment.DeviceSettingFragment;
import com.onyx.android.plato.fragment.EmptyFragment;
import com.onyx.android.plato.fragment.FillHomeworkFragment;
import com.onyx.android.plato.fragment.FinishedFragment;
import com.onyx.android.plato.fragment.GoalAdvancedFragment;
import com.onyx.android.plato.fragment.MainFragment;
import com.onyx.android.plato.fragment.ParseAnswerFragment;
import com.onyx.android.plato.fragment.RankingFragment;
import com.onyx.android.plato.fragment.RemindFragment;
import com.onyx.android.plato.fragment.ReportFragment;
import com.onyx.android.plato.fragment.StudyReportFragment;
import com.onyx.android.plato.fragment.UnfinishedFragment;
import com.onyx.android.plato.fragment.UserCenterFragment;
import com.onyx.android.plato.interfaces.MainView;
import com.onyx.android.plato.presenter.MainPresenter;
import com.onyx.android.plato.utils.ApkUtils;
import com.onyx.android.plato.utils.SystemUtils;
import com.onyx.android.sdk.data.model.ApplicationUpdate;
import com.onyx.android.sdk.data.model.Firmware;
import com.onyx.android.sdk.data.request.cloud.FirmwareUpdateRequest;
import com.onyx.android.sdk.utils.PreferenceManager;
import com.onyx.android.sdk.utils.StringUtils;

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
    private int lastPageId = ChildViewID.BASE_VIEW;
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
    private int oldTabPosition = 0;
    private int oldPageID = ChildViewID.FRAGMENT_MAIN;
    private MainPresenter mainPresenter;
    private List<ContentBean> remindContent;

    @Override
    protected void initData() {
        restoreUserName();
        currentTitleIconID = userCenterTitleIconID;
        userCenterFragmentTitle = getString(R.string.user_center_title);
        changePasswordFragmentTitle = getString(R.string.user_center_fragment_change_password);
        mainPresenter = new MainPresenter(this);
        //TODO:fake student id 135
        mainPresenter.getNewMessage(SunApplication.getStudentId() + "");
    }

    private void restoreUserName() {
        String name = PreferenceManager.getStringValue(SunApplication.getInstance(), Constants.SP_KEY_USER_NAME, "");
        if (!TextUtils.isEmpty(name)) {
            mainFragmentTitle = name + getString(R.string.main_activity_hello);
            currentTitle = mainFragmentTitle;
        }
    }

    @Override
    protected void initView(ViewDataBinding binding) {
        mainBinding = (ActivityMainBinding) binding;
        mainBinding.setVariable(BR.presenter, mainPresenter);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
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
                switchCurrentFragment(ChildViewID.FRAGMENT_REMIND);
                RemindFragment remindFragment = (RemindFragment) getPageView(ChildViewID.FRAGMENT_REMIND);
                remindFragment.setRemindContent(remindContent);
                break;
            case R.id.ll_main_activity_title_container:
                onClickTitleContainer();
                break;
        }
    }

    private void onClickTitleContainer() {
        switch (currentPageID) {
            case ChildViewID.FRAGMENT_USER_CENTER:
                switchToOldFragment();
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

    private void switchToOldFragment() {
        switchCurrentFragment(oldPageID);
        mainBinding.mainActivityTab.getTabAt(oldTabPosition).select();
    }

    private void setTitleAndIcon() {
        switch (currentPageID) {
            case ChildViewID.FRAGMENT_CHANGE_PASSWORD:
                changeTitleParams(false, backTitleIconID, changePasswordFragmentTitle);
                break;
            case ChildViewID.FRAGMENT_USER_CENTER:
                changeTitleParams(false, backTitleIconID, userCenterFragmentTitle);
                break;
            default:
                changeTitleParams(true, userCenterTitleIconID, mainFragmentTitle);
                break;
        }
        mainBinding.setIsShowTabLayoutAndNewMessageView(isShowTabLayoutAndNewMessageView);
        mainBinding.ivMainActivityTitleIcon.setImageResource(currentTitleIconID);
        mainBinding.setTitle(currentTitle);
    }

    private void changeTitleParams(boolean isShowTabLayoutAndNewMessageView, int currentTitleIconID, String currentTitle) {
        this.isShowTabLayoutAndNewMessageView = isShowTabLayoutAndNewMessageView;
        this.currentTitleIconID = currentTitleIconID;
        this.currentTitle = currentTitle;
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
        if (currentPageID != ChildViewID.FRAGMENT_CHANGE_PASSWORD && currentPageID != ChildViewID.FRAGMENT_USER_CENTER) {
            oldPageID = currentPageID;
            oldTabPosition = mainBinding.mainActivityTab.getSelectedTabPosition();
        }
        lastPageId = currentPageID;
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
                case ChildViewID.FRAGMENT_GOAL_ADVANCED:
                    baseFragment = new GoalAdvancedFragment();
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
                case ChildViewID.FRAGMENT_PARSE_ANSWER:
                    baseFragment = new ParseAnswerFragment();
                    break;
                case ChildViewID.FRAGMENT_USER_CENTER:
                    baseFragment = new UserCenterFragment();
                    break;
                case ChildViewID.FRAGMENT_CHANGE_PASSWORD:
                    baseFragment = new ChangePasswordFragment();
                    break;
                case ChildViewID.FRAGMENT_STUDY_REPORT:
                    baseFragment = new StudyReportFragment();
                    break;
                case ChildViewID.FRAGMENT_REMIND:
                    baseFragment = new RemindFragment();
                    break;
                case ChildViewID.FRAGMENT_EMPTY:
                    baseFragment = new EmptyFragment();
                    break;
                case ChildViewID.FRAGMENT_UNFINISHED:
                    baseFragment = new UnfinishedFragment();
                    break;
                case ChildViewID.FRAGMENT_FINISHED:
                    baseFragment = new FinishedFragment();
                    break;
                case ChildViewID.FRAGMENT_REPORT:
                    baseFragment = new ReportFragment();
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
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToHomeworkEvent(ToHomeworkEvent event) {
        mainBinding.mainActivityTab.getTabAt(ChildViewID.FRAGMENT_UNFINISHED).select();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeworkUnfinishedEvent(HomeworkUnfinishedEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_UNFINISHED);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeworkFinishedEvent(HomeworkFinishedEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_FINISHED);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onHomeworkReportEvent(HomeworkReportEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_REPORT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDeleteRemindEvent(DeleteRemindEvent event) {
        //TODO:fake student id 135
        mainPresenter.deleteRemindMessage(event.getRemindId() + "", SunApplication.getStudentId() + "");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnfinishedEvent(UnfinishedEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_FILL_HOMEWORK);
        FillHomeworkFragment fillHomeworkFragment = (FillHomeworkFragment) getPageView(ChildViewID.FRAGMENT_FILL_HOMEWORK);
        fillHomeworkFragment.setTaskId(event.getId(), event.getPracticeId(), event.getType(), event.getTitle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshFragmentEvent(RefreshFragmentEvent event) {
        switchCurrentFragment(lastPageId);
        BaseFragment baseFragment = getPageView(currentPageID);
        baseFragment.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEmptyEvent(EmptyEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_EMPTY);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackToHomeworkFragmentEvent(BackToHomeworkFragmentEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_UNFINISHED);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToCorrectEvent(ToCorrectEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_CORRECT);
        CorrectFragment correctFragment = (CorrectFragment) getPageView(ChildViewID.FRAGMENT_CORRECT);
        ContentBean content = event.getContent();
        if (content != null) {
            correctFragment.setStartTimer(content);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToRankingEvent(ToRankingEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_RANKING);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToParseAnswerEvent(ParseAnswerEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_PARSE_ANSWER);
        ParseAnswerFragment parseAnswerFragment = (ParseAnswerFragment) getPageView(ChildViewID.FRAGMENT_PARSE_ANSWER);
        QuestionViewBean questionViewBean = event.getQuestion();
        String title = event.getTitle();
        if (questionViewBean != null && !StringUtils.isNullOrEmpty(title)) {
            parseAnswerFragment.setQuestionData(questionViewBean, title);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onToMainFragmentEvent(ToMainFragmentEvent event) {
        mainBinding.mainActivityTab.getTabAt(ChildViewID.FRAGMENT_MAIN).select();
        switchCurrentFragment(ChildViewID.FRAGMENT_MAIN);
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
    public void onToStudyReportDeatilEvent(ToStudyReportDeatilEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_STUDY_REPORT);
        StudyReportFragment studyReportFragment = (StudyReportFragment) getPageView(ChildViewID.FRAGMENT_STUDY_REPORT);
        studyReportFragment.setPracticeId(event.getId(), event.getTitle());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBackPressEvent(OnBackPressEvent event) {
        switch (event.childViewId) {
            case ChildViewID.FRAGMENT_STUDY_REPORT:
                onBackToHomeworkFragmentEvent(null);
                break;
            case ChildViewID.FRAGMENT_USER_CENTER:
                onToMainFragmentEvent(null);
                break;
        }
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

    @Override
    public void setRemindContent(List<ContentBean> content) {
        this.remindContent = content;
        mainBinding.setHaveNew(true);
    }

    @Override
    public void setRemindView() {
        mainBinding.setHaveNew(false);
        if (remindContent != null) {
            remindContent.clear();
        }
    }
}
