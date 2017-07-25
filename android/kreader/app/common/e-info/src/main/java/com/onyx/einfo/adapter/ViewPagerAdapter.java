package com.onyx.einfo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.onyx.android.sdk.data.model.Library;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.einfo.fragment.AccountFragment;
import com.onyx.einfo.fragment.ContentFragment;
import com.onyx.einfo.model.TabAction;
import com.onyx.einfo.model.TabLibrary;

import java.util.List;

/**
 * Created by suicheng on 2017/7/20.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private List<TabLibrary> pageTabList;

    public ViewPagerAdapter(List<TabLibrary> pageTabList, FragmentManager fm) {
        super(fm);
        this.pageTabList = pageTabList;
    }

    private ContentFragment getCommonContentFragment(String fragmentName, Library library) {
        String libraryId = null;
        if (library != null) {
            libraryId = library.getIdString();
        }
        return ContentFragment.newInstance(fragmentName, libraryId);
    }

    private Fragment getContentFragment(Library library) {
        return getCommonContentFragment(library.getName(), library);
    }

    @Override
    public Fragment getItem(int position) {
        TabLibrary tabLibrary = pageTabList.get(position);
        Fragment f = new Fragment();
        if (tabLibrary.library != null) {
            f = getContentFragment(tabLibrary.library);
        } else {
            if (tabLibrary.action == TabAction.Account) {
                f = AccountFragment.newInstance();
            }
        }
        return f;
    }

    @Override
    public int getCount() {
        return CollectionUtils.getSize(pageTabList);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        TabLibrary tabLibrary = pageTabList.get(position);
        return tabLibrary.getTabTitle();
    }
}
