package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.FrenchQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public class DictTypeConfig {

    public List<DictTypeData> dictLanguageData = new ArrayList<>();

    public void loadDictInfo(Context context) {

        DictTypeData dictData = new DictTypeData(context.getResources().getString(R.string.english_query), new EnglishQueryEvent());
        dictLanguageData.add(dictData);

        dictData = new DictTypeData(context.getResources().getString(R.string.chinese_query), new ChineseQueryEvent());
        dictLanguageData.add(dictData);

        dictData = new DictTypeData(context.getResources().getString(R.string.japanese_query), new JapaneseQueryEvent());
        dictLanguageData.add(dictData);

        dictData = new DictTypeData(context.getResources().getString(R.string.french_query), new FrenchQueryEvent());
        dictLanguageData.add(dictData);
    }

    public List<DictTypeData> getDictTypeData(int userType) {
        switch (userType) {
            case Constants.ACCOUNT_TYPE_DICT_LANGUAGE:
                return dictLanguageData;
        }
        return dictLanguageData;
    }
}
