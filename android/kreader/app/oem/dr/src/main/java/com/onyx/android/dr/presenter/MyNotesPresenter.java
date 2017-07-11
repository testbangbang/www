package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.ＭyNotesTypeConfig;
import com.onyx.android.dr.interfaces.MyNotesView;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MyNotesPresenter {
    private final ＭyNotesTypeConfig myNotesTypeConfig;
    private MyNotesView myNotesView;

    public MyNotesPresenter(MyNotesView myNotesView) {
        this.myNotesView = myNotesView;
        myNotesTypeConfig = new ＭyNotesTypeConfig();
    }

    public void loadData(Context context) {
        myNotesTypeConfig.loadDictInfo(context);
    }

    public void loadMyNotesType(int userType) {
        myNotesView.setMyNotesTypeData(myNotesTypeConfig.getMenuData(userType));
    }
}
