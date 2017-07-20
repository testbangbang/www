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
    private Context context;

    public GoodSentenceTypePresenter(Context context, GoodSentenceTpyeView goodSentenceTpyeView) {
        this.goodSentenceTpyeView = goodSentenceTpyeView;
        this.context = context;
        goodSentenceTypeConfig = new GoodSentenceTypeConfig();
    }

    public void loadGoodSentenceData(String englishUsed, String chineseUsed, String otherLanguageUsed) {
        goodSentenceTypeConfig.loadGoodSentenceInfo(context, englishUsed, chineseUsed, otherLanguageUsed);
    }

    public void loadNewWordData( String englishUsed, String chineseUsed, String otherLanguageUsed) {
        goodSentenceTypeConfig.loadNewWordInfo(context, englishUsed, chineseUsed, otherLanguageUsed);
    }

    public void loadDataByType(int type) {
        goodSentenceTpyeView.setGoodSentenceTpyeData(goodSentenceTypeConfig.getDatas(type));
    }
}
