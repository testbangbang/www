package com.onyx.edu.student.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.onyx.android.sdk.data.Constant;
import com.onyx.android.sdk.data.utils.JSONObjectParseUtils;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.edu.student.model.TabItem;

import java.util.List;

/**
 * Created by suicheng on 2017/7/20.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public static final String ARGS = Constant.ARGS_TAG;

    private List<TabItem> pageTabList;
    private Context context;

    public ViewPagerAdapter(Context context, List<TabItem> pageTabList, FragmentManager fm) {
        super(fm);
        this.context = context.getApplicationContext();
        this.pageTabList = pageTabList;
    }

    @Override
    public Fragment getItem(int position) {
        TabItem tabItem = pageTabList.get(position);
        Fragment f = Fragment.instantiate(context, tabItem.fragmentClass.getName());
        if (tabItem.extraArgument != null) {
            Bundle bundle = new Bundle();
            bundle.putString(ARGS, JSONObjectParseUtils.toJson(tabItem.extraArgument));
            f.setArguments(bundle);
        }
        return f;
    }

    @Override
    public int getCount() {
        return CollectionUtils.getSize(pageTabList);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTabList.get(position).getTabTitle();
    }
}
