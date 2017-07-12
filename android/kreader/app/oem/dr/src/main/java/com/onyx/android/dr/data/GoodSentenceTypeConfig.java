package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.GoodSentenceTypeBean;
import com.onyx.android.dr.event.ChineseGoodSentenceEvent;
import com.onyx.android.dr.event.EnglishGoodSentenceEvent;
import com.onyx.android.dr.event.MinorityLanguageGoodSentenceEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public class GoodSentenceTypeConfig {
    public List<GoodSentenceTypeBean> goodSentenceTypeList = new ArrayList<>();

    public void loadDictInfo(Context context) {

        GoodSentenceTypeBean goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.english_good_sentence_notebook),new EnglishGoodSentenceEvent());
        goodSentenceTypeList.add(goodSentenceTypeData);

        goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.chinese_good_sentence_notebook),   new ChineseGoodSentenceEvent());
        goodSentenceTypeList.add(goodSentenceTypeData);

        goodSentenceTypeData = new GoodSentenceTypeBean(context.getResources().getString(R.string.minority_language_good_sentence_notebook),   new MinorityLanguageGoodSentenceEvent());
        goodSentenceTypeList.add(goodSentenceTypeData);
    }

    public List<GoodSentenceTypeBean> getDatas() {
        return goodSentenceTypeList;
    }
}
