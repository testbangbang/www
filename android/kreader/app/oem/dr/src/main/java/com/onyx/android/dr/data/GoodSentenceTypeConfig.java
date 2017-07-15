package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GoodSentenceTypeBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ChineseGoodSentenceEvent;
import com.onyx.android.dr.event.ChineseNewWordEvent;
import com.onyx.android.dr.event.EnglishGoodSentenceEvent;
import com.onyx.android.dr.event.EnglishNewWordEvent;
import com.onyx.android.dr.event.MinorityLanguageGoodSentenceEvent;
import com.onyx.android.dr.event.MinorityLanguageNewWordEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public class GoodSentenceTypeConfig {
    public List<GoodSentenceTypeBean> goodSentenceTypeList = new ArrayList<>();
    public List<GoodSentenceTypeBean> newWordTypeList = new ArrayList<>();

    public void loadGoodSentenceInfo(Context context, String englishUsed, String chineseUsed, String otherLanguageUsed) {

        GoodSentenceTypeBean goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.english_good_sentence_notebook) + "  " + englishUsed, new EnglishGoodSentenceEvent());
        goodSentenceTypeList.add(goodSentenceTypeData);

        goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.chinese_good_sentence_notebook) + "  " + chineseUsed, new ChineseGoodSentenceEvent());
        goodSentenceTypeList.add(goodSentenceTypeData);

        goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.minority_language_good_sentence_notebook) + "  " + otherLanguageUsed, new MinorityLanguageGoodSentenceEvent());
        goodSentenceTypeList.add(goodSentenceTypeData);
    }

    public void loadNewWordInfo(Context context, String englishUsed, String chineseUsed, String otherLanguageUsed) {

        GoodSentenceTypeBean goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.english_new_word_notebook) + "  " + englishUsed, new EnglishNewWordEvent());
        newWordTypeList.add(goodSentenceTypeData);

        goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.chinese_new_word_notebook) + "  " + englishUsed, new ChineseNewWordEvent());
        newWordTypeList.add(goodSentenceTypeData);

        goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.minority_language_new_word_notebook) + "  " + englishUsed, new MinorityLanguageNewWordEvent());
        newWordTypeList.add(goodSentenceTypeData);
    }

    public List<GoodSentenceTypeBean> getDatas(int type) {
        switch (type) {
            case Constants.ACCOUNT_TYPE_GOOD_SENTENCE:
                return goodSentenceTypeList;
            case Constants.ACCOUNT_TYPE_NEW_WORD:
                return newWordTypeList;
        }
        return goodSentenceTypeList;
    }
}
