package com.onyx.android.sun.common;

import android.content.Context;

import com.onyx.android.sun.R;
import com.onyx.android.sun.bean.MainTabBean;
import com.onyx.android.sun.fragment.ChildViewID;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehai on 17-9-29.
 */

public class AppConfigData {
    private static List<MainTabBean> mainTabList = new ArrayList();

    public static void loadMainTabData(Context context) {
        mainTabList.add(new MainTabBean(context.getString(R.string.main_view), ChildViewID.FRAGMENT_MAIN));
        mainTabList.add(new MainTabBean(context.getString(R.string.examination_work), ChildViewID.FRAGMENT_EXAMINATION_WORK));
        mainTabList.add(new MainTabBean(context.getString(R.string.goal_advanced), ChildViewID.FRAGMENT_GOAL_ADVANCED));
        mainTabList.add(new MainTabBean(context.getString(R.string.study_management), ChildViewID.FRAGMENT_STUDY_MANAGEMENT));
    }

    public static List<MainTabBean> getMainTabList() {
        return mainTabList;
    }
}
