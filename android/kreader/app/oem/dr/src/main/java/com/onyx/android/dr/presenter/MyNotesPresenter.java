package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.MyNotesTypeConfig;
import com.onyx.android.dr.interfaces.MyNotesView;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class MyNotesPresenter {
    private final MyNotesTypeConfig myNotesTypeConfig;
    private MyNotesView myNotesView;

    public MyNotesPresenter(MyNotesView myNotesView) {
        this.myNotesView = myNotesView;
        myNotesTypeConfig = new MyNotesTypeConfig();
    }

    public void loadData(Context context) {
        myNotesTypeConfig.loadDictInfo(context);
    }

    public void loadMyTracks(int userType) {
        myNotesView.setMyracksData(myNotesTypeConfig.getMenuData(userType));
    }
    public void loadMyThink(int userType) {
        myNotesView.setMyThinkData(myNotesTypeConfig.getMenuData(userType));
    }
    public void loadMyCreation(int userType) {
        myNotesView.setMyCreationData(myNotesTypeConfig.getMenuData(userType));
    }
}
