package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.HearAndSpeakConfig;
import com.onyx.android.dr.interfaces.HearAndSpeakView;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class HearAndSpeakPresenter {
    private final HearAndSpeakConfig hearAndSpeakConfig;
    private HearAndSpeakView hearAndSpeakView;
    private Context context;

    public HearAndSpeakPresenter(Context context, HearAndSpeakView hearAndSpeakView) {
        this.hearAndSpeakView = hearAndSpeakView;
        this.context = context;
        hearAndSpeakConfig = new HearAndSpeakConfig();
    }

    public void loadData(Context context) {
        hearAndSpeakConfig.loadDictInfo(context);
    }

    public void loadHearAndSpeakData(int userType) {
        hearAndSpeakView.setHearAndSpeakData(hearAndSpeakConfig.getMenuData(userType));
    }
}
