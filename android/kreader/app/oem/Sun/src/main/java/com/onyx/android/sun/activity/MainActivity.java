package com.onyx.android.sun.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.databinding.ViewDataBinding;
import android.support.design.widget.TabLayout;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.bean.MainTabBean;
import com.onyx.android.sun.bean.User;
import com.onyx.android.sun.common.AppConfigData;
import com.onyx.android.sun.databinding.ActivityMainBinding;
import com.onyx.android.sun.event.FillHomeworkEvent;
import com.onyx.android.sun.event.UnfinishedEvent;
import com.onyx.android.sun.fragment.BaseFragment;
import com.onyx.android.sun.fragment.ChildViewID;
import com.onyx.android.sun.fragment.FillHomeworkFragment;
import com.onyx.android.sun.fragment.HomeWorkFragment;
import com.onyx.android.sun.fragment.MainFragment;
import com.onyx.android.sun.interfaces.MainView;
import com.onyx.android.sun.presenter.MainPresenter;

import org.greenrobot.eventbus.EventBus;
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

    @Override
    protected void initData() {

    }

    @Override
    protected void initView(ViewDataBinding binding) {
        mainBinding = (ActivityMainBinding) binding;
        mainBinding.setVariable(BR.presenter, new MainPresenter());
        mainBinding.setVariable(BR.user, user);
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
                break;
        }
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
            }
        } else {
            baseFragment.isStored = true;
        }
        return baseFragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnfinishedEvent(UnfinishedEvent event) {
        switchCurrentFragment(ChildViewID.FRAGMENT_FILL_HOMEWORK);
        EventBus.getDefault().post(new FillHomeworkEvent(event.getId(), event.getType(), event.getTitle()));
    }
}
