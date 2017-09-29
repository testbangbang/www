package com.onyx.android.sun.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;
import com.onyx.android.sun.R;
import com.onyx.android.sun.bean.MainTabBean;
import com.onyx.android.sun.bean.User;
import com.onyx.android.sun.common.AppConfigData;
import com.onyx.android.sun.databinding.ActivityMainBinding;
import com.onyx.android.sun.fragment.BaseFragment;
import com.onyx.android.sun.fragment.ChildViewID;
import com.onyx.android.sun.fragment.MainFragment;
import com.onyx.android.sun.interfaces.MainView;
import com.onyx.android.sun.presenter.MainPresenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements MainView, View.OnClickListener {

    private ActivityMainBinding mainBinding;
    private User user = new User();
    private FragmentManager fragmentManager;
    private int currentPageID = ChildViewID.BASE_VIEW;
    private FragmentTransaction transaction;
    private BaseFragment currentFragment;
    private Map<Integer, BaseFragment> childViewList = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mainBinding.setVariable(BR.presenter, new MainPresenter());
        mainBinding.setVariable(BR.user, user);
        mainBinding.setListener(this);
        initView();
        initListener();
    }

    private void initView() {
        List<MainTabBean> mainTabList = AppConfigData.getMainTabList();
        for (MainTabBean mainTabBean : mainTabList) {
            TabLayout.Tab tab = mainBinding.mainActivityTab.newTab();
            tab.setText(mainTabBean.tabName);
            tab.setTag(mainTabBean.tag);
            mainBinding.mainActivityTab.addTab(tab);
        }
    }

    private void initListener() {
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

        mainBinding.newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
                    baseFragment = new MainFragment();
                    break;
                case ChildViewID.FRAGMENT_GOAL_ADVANCED:
                    baseFragment = new MainFragment();
                    break;
                case ChildViewID.FRAGMENT_STUDY_MANAGEMENT:
                    baseFragment = new MainFragment();
                    break;
            }
        } else {
            baseFragment.isStored = true;
        }
        return baseFragment;
    }
}
