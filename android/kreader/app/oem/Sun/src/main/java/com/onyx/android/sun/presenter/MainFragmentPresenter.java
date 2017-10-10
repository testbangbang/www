package com.onyx.android.sun.presenter;

import com.onyx.android.sun.data.MainFragmentData;
import com.onyx.android.sun.interfaces.MainFragmentView;

/**
 * Created by hehai on 17-9-29.
 */

public class MainFragmentPresenter {
    private MainFragmentView mainFragmentView;
    private MainFragmentData mainFragmentData;

    public MainFragmentPresenter(MainFragmentView mainFragmentView) {
        this.mainFragmentView = mainFragmentView;
        mainFragmentData = new MainFragmentData();
    }

    public void getPractices() {

    }

    public void getSubjectScore() {
        mainFragmentView.setSubjectScore(mainFragmentData.getSubjectScoreMap());
    }
}
