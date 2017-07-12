package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.data.GoodSentenceTypeConfig;
import com.onyx.android.dr.interfaces.GoodSentenceTpyeView;

/**
 * Created by zhouzhiming on 17-7-11.
 */
public class GoodSentenceTypePresenter {
    private final GoodSentenceTypeConfig goodSentenceTypeConfig;
    private GoodSentenceTpyeView goodSentenceTpyeView;

    public GoodSentenceTypePresenter(GoodSentenceTpyeView goodSentenceTpyeView) {
        this.goodSentenceTpyeView = goodSentenceTpyeView;
        goodSentenceTypeConfig = new GoodSentenceTypeConfig();
    }

    public void loadData(Context context) {
        goodSentenceTypeConfig.loadDictInfo(context);
    }

    public void loadGoodSentenceType() {
        goodSentenceTpyeView.setGoodSentenceTpyeData(goodSentenceTypeConfig.getDatas());
    }
}
