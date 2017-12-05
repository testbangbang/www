package com.onyx.android.plato.common;

import android.content.Context;

import com.onyx.android.plato.R;
import com.onyx.android.plato.bean.MainTabBean;
import com.onyx.android.plato.fragment.ChildViewID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-9-29.
 */

public class AppConfigData {
    private static List<MainTabBean> mainTabList = new ArrayList();

    public static void loadMainTabData(Context context) {
        mainTabList.add(new MainTabBean(context.getString(R.string.main_view), ChildViewID.FRAGMENT_MAIN));
        mainTabList.add(new MainTabBean(context.getString(R.string.examination_work), ChildViewID.FRAGMENT_UNFINISHED));
        mainTabList.add(new MainTabBean(context.getString(R.string.goal_advanced), ChildViewID.FRAGMENT_GOAL_ADVANCED));
        mainTabList.add(new MainTabBean(context.getString(R.string.study_management), ChildViewID.FRAGMENT_STUDY_MANAGEMENT));
    }

    public static List<MainTabBean> getMainTabList() {
        return mainTabList;
    }
}
