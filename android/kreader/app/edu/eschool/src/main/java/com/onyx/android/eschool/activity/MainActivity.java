package com.onyx.android.eschool.activity;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.onyx.android.eschool.R;
import com.onyx.android.eschool.custom.NoSwipePager;
import com.onyx.android.eschool.fragment.BookTextFragment;
import com.onyx.android.sdk.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

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
    private BookTextFragment homeworkFragment;

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
    protected void initData() {
        bookTextFragment = BookTextFragment.newInstance("5916a8a1fcb81900011feb03");
        teachingAuxiliaryFragment = BookTextFragment.newInstance(null);
        bookReadingFragment = BookTextFragment.newInstance(null);
        homeworkFragment = BookTextFragment.newInstance(null);
    }

    private void initViewPager() {
        pageAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerView.addFilterScrollableViewClass(RecyclerView.class);
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

    private void selectTabFromPosition(TabLayout tabLayout, int position) {
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

    private void initTableView() {
        titleList.add("课本");
        titleList.add("辅导");
        titleList.add("阅读");
        titleList.add("作业");
        contentTabLayout.setTabMode(TabLayout.MODE_FIXED);
        contentTabLayout.setSelectedTabIndicatorHeight(0);
        for (String title : titleList) {
            TabLayout.Tab tab = contentTabLayout.newTab().setCustomView(R.layout.tab_item_layout);
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.title_text);
            textView.setText(title);
            contentTabLayout.addTab(tab);
        }
        selectTabFromPosition(contentTabLayout, 0);
        contentTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTabFromPosition(contentTabLayout, tab.getPosition());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f = bookTextFragment;
            switch (position) {
                case 0:
                    f = bookTextFragment;
                    break;
                case 1:
                    f = teachingAuxiliaryFragment;
                    break;
                case 2:
                    f = bookReadingFragment;
                    break;
                case 3:
                    f = homeworkFragment;
                    break;
            }
            return f;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }
    }
}
