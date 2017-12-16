package com.onyx.android.eschool.activity;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.SchoolApp;
import com.onyx.android.eschool.action.ActionChain;
import com.onyx.android.eschool.action.AuthTokenAction;
import com.onyx.android.eschool.action.CloudLibraryListLoadAction;
import com.onyx.android.eschool.custom.NoSwipePager;
import com.onyx.android.eschool.device.DeviceConfig;
import com.onyx.android.eschool.events.BookLibraryEvent;
import com.onyx.android.eschool.events.DataRefreshEvent;
import com.onyx.android.eschool.events.TabSwitchEvent;
import com.onyx.android.eschool.fragment.AccountFragment;
import com.onyx.android.eschool.fragment.BookTextFragment;
import com.onyx.android.eschool.fragment.HomeworkFragment;
import com.onyx.android.eschool.utils.StudentPreferenceManager;
import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.api.device.epd.UpdateMode;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;
import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.android.sdk.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by suicheng on 2017/5/13.
 */

public class MainActivity extends BaseActivity {

    @Bind(R.id.contentTab)
    TabLayout contentTabLayout;
    @Bind(R.id.contentViewPager)
    NoSwipePager pagerView;
    private ViewPagerAdapter pageAdapter;
    private List<String> titleList = new ArrayList<>();

    private BookTextFragment bookTextFragment;
    private BookTextFragment teachingAuxiliaryFragment;
    private BookTextFragment bookReadingFragment;
    private HomeworkFragment homeworkFragment;
    private AccountFragment studentInfoFragment;

    private Map<String, Library> libraryMap = new HashMap<>();

    @Override
    protected Integer getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initConfig() {
    }

    @Override
    protected void initView() {
        initTableView();
        initViewPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DeviceConfig.sharedInstance(getApplicationContext()).enableFullRefresh()) {
            EpdController.postInvalidate(getWindow().getDecorView().getRootView(), UpdateMode.GC);
        }
    }

    private String getLibraryParentId() {
        return StudentPreferenceManager.loadLibraryParentId(this, null);
    }

    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {
        final AuthTokenAction authTokenAction = new AuthTokenAction();
        final CloudLibraryListLoadAction loadAction = new CloudLibraryListLoadAction(getLibraryParentId());
        ActionChain actionChain = new ActionChain();
        actionChain.addAction(authTokenAction);
        actionChain.addAction(loadAction);
        actionChain.execute(SchoolApp.getLibraryDataHolder(), new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                if (e != null) {
                    return;
                }
                final List<Library> list = loadAction.getLibraryList();
                notifyDataChanged(list);
            }
        });
    }

    private void notifyDataChanged(List<Library> list) {
        for (Library library : list) {
            if (!Library.isValid(library)) {
                Log.e(getClass().getSimpleName(), "detected the invalid library");
                continue;
            }
            addToLibraryMap(library);
            EventBus.getDefault().postSticky(new BookLibraryEvent(library));
        }
    }

    private void addToLibraryMap(Library library) {
        libraryMap.put(library.getName(), library);
    }

    private void initViewPager() {
        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerView.addFilterScrollableViewClass(RecyclerView.class);
        pagerView.addFilterScrollableViewClass(ViewPager.class);
        pagerView.setAdapter(pageAdapter);
        pagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (contentTabLayout.getSelectedTabPosition() == CollectionUtils.getSize(titleList) - 1) {
            EventBus.getDefault().post(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (filterKeyEvent(event)) {
            return super.dispatchKeyEvent(event);
        }
        EventBus.getDefault().post(event);
        return true;
    }

    private boolean filterKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK || event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return false;
    }

    private void selectTab(TabLayout tabLayout, int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View itemView = tabLayout.getTabAt(i).getCustomView();
            TextView textView = (TextView) itemView.findViewById(R.id.title_text);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.action_image);
            if (position == i) {
                textView.setTextColor(Color.WHITE);
                imageView.setVisibility(View.VISIBLE);
            } else {
                textView.setTextColor(Color.BLACK);
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void addTabList(TabLayout tabLayout, List<String> tabTitleList) {
        for (String title : tabTitleList) {
            TabLayout.Tab tab = tabLayout.newTab().setCustomView(R.layout.tab_item_layout);
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.title_text);
            textView.setText(title);
            tabLayout.addTab(tab);
        }
    }

    private void initTableView() {
        titleList.add("课本");
        titleList.add("作业");
        titleList.add("辅导");
        titleList.add("阅读");
        titleList.add("个人信息");
        contentTabLayout.setTabMode(TabLayout.MODE_FIXED);
        contentTabLayout.setSelectedTabIndicatorHeight(0);
        disableA2ForSpecificView(contentTabLayout);
        addTabList(contentTabLayout, titleList);
        selectTab(contentTabLayout, 0);
        contentTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(contentTabLayout, tab.getPosition());
                pagerView.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void disableA2ForSpecificView(View view) {
        Device.currentDevice().disableA2ForSpecificView(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDataRefreshEvent(DataRefreshEvent event) {
        loadData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabSwitchEvent(TabSwitchEvent switchEvent) {
        processTabSwitchEvent(switchEvent);
    }

    private void processTabSwitchEvent(TabSwitchEvent switchEvent) {
        int position = contentTabLayout.getSelectedTabPosition();
        if (switchEvent.isNextTabSwitch()) {
            position++;
        } else {
            position--;
        }
        switchToNewTab(position);
    }

    private void switchToNewTab(int position) {
        TabLayout.Tab tab = contentTabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }

    private BookTextFragment getCommonBookFragment(String fragmentName, Library library) {
        String libraryId = null;
        if (library != null) {
            libraryId = library.getIdString();
        }
        return BookTextFragment.newInstance(fragmentName, libraryId);
    }

    private Fragment getBookTextFragment(String libraryName, Library library) {
        if (bookTextFragment == null) {
            bookTextFragment = getCommonBookFragment(libraryName, library);
        }
        return bookTextFragment;
    }

    public BookTextFragment getTeachingAuxiliaryFragment(String libraryName, Library library) {
        if (teachingAuxiliaryFragment == null) {
            teachingAuxiliaryFragment = getCommonBookFragment(libraryName, library);
        }
        return teachingAuxiliaryFragment;
    }

    public BookTextFragment getBookReadingFragment(String libraryName, Library library) {
        if (bookReadingFragment == null) {
            bookReadingFragment = getCommonBookFragment(libraryName, library);
        }
        return bookReadingFragment;
    }

    public Fragment getHomeworkFragment() {
        if (homeworkFragment == null) {
            homeworkFragment = HomeworkFragment.newInstance();
        }
        return homeworkFragment;
    }

    private Fragment getStudentFragment() {
        if (studentInfoFragment == null) {
            studentInfoFragment = AccountFragment.newInstance();
        }
        return studentInfoFragment;
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String title = getPageTitle(position).toString();
            Library library = libraryMap.get(title);
            Fragment f = getBookTextFragment(title, library);
            switch (position) {
                case 0:
                    f = getBookTextFragment(title, library);
                    break;
                case 1:
                    f = getHomeworkFragment();
                    break;
                case 2:
                    f = getTeachingAuxiliaryFragment(title, library);
                    break;
                case 3:
                    f = getBookReadingFragment(title, library);
                    break;
                case 4:
                    f = getStudentFragment();
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return CollectionUtils.getSize(titleList);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}
