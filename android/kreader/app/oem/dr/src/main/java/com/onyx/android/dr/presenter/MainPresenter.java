package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.activity.MainView;
import com.onyx.android.dr.data.MainData;
import com.onyx.android.dr.data.MainTabMenuConfig;

/**
 * Created by hehai on 17-6-28.
 */

public class MainPresenter {
    private MainView mainView;
    private MainData mainData;

    public MainPresenter(MainView mainView) {
        this.mainView = mainView;
        mainData = new MainData();
    }

    public void loadData(Context context) {
        MainTabMenuConfig.loadMenuInfo(context);
    }

    public void loadTabMenu(int userType) {
        mainView.setTabMenuData(MainTabMenuConfig.getMenuData(userType));
    }
}
