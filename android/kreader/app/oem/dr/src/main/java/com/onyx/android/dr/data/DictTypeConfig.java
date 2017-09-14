package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;
import com.onyx.android.dr.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by zhouzhiming on 17-6-28.
 */
public class DictTypeConfig {
    public static List<DictTypeBean> dictLanguageData = new ArrayList<>();
    public static Map<Integer, List<DictTypeBean>> englishDictMap = new ConcurrentHashMap<>();
    public static Map<Integer, List<DictTypeBean>> chineseDictMap = new ConcurrentHashMap<>();
    public static Map<Integer, List<DictTypeBean>> minorityDictMap = new ConcurrentHashMap<>();

    public void loadDictInfo(Context context) {
        dictLanguageData.clear();
        DictTypeBean dictData = new DictTypeBean(context.getResources().getString(R.string.english_query), new EnglishQueryEvent(), Constants.ENGLISH_TYPE);
        dictLanguageData.add(dictData);

        dictData = new DictTypeBean(context.getResources().getString(R.string.chinese_query_content), new ChineseQueryEvent(), Constants.CHINESE_TYPE);
        dictLanguageData.add(dictData);

        dictData = new DictTypeBean(context.getResources().getString(R.string.japanese_query_content), new JapaneseQueryEvent(), Constants.OTHER_TYPE);
        dictLanguageData.add(dictData);
    }

    public void loadDictMap() {
        if (englishDictMap == null || englishDictMap.size() <= 0) {
            List<DictTypeBean> dictName = Utils.getDictName(Constants.ENGLISH_DICTIONARY);
            List<DictTypeBean> newDictName = new ArrayList<>();
            if (dictName != null && dictName.size() > Constants.FOUR) {
                for (int i = 0; i < Constants.FOUR; i++) {
                    if (!newDictName.contains(dictName.get(i))){
                        newDictName.add(dictName.get(i));
                    }
                }
                englishDictMap.put(Constants.ENGLISH_TYPE, newDictName);
            }else{
                englishDictMap.put(Constants.ENGLISH_TYPE, Utils.getDictName(Constants.ENGLISH_DICTIONARY));
            }
        }
        if (chineseDictMap == null || chineseDictMap.size() <= 0) {
            List<DictTypeBean> dictName = Utils.getDictName(Constants.CHINESE_DICTIONARY);
            List<DictTypeBean> newDictName = new ArrayList<>();
            if (dictName != null && dictName.size() > Constants.FOUR) {
                for (int i = 0; i < Constants.FOUR; i++) {
                    if (!newDictName.contains(dictName.get(i))){
                        newDictName.add(dictName.get(i));
                    }
                }
                chineseDictMap.put(Constants.CHINESE_TYPE, newDictName);
            }else{
                chineseDictMap.put(Constants.CHINESE_TYPE, Utils.getDictName(Constants.CHINESE_DICTIONARY));
            }
        }
        if (minorityDictMap == null || minorityDictMap.size() <= 0) {
            List<DictTypeBean> dictName = Utils.getDictName(Constants.OTHER_DICTIONARY);
            List<DictTypeBean> newDictName = new ArrayList<>();
            if (dictName != null && dictName.size() > Constants.FOUR) {
                for (int i = 0; i < Constants.FOUR; i++) {
                    if (!newDictName.contains(dictName.get(i))){
                        newDictName.add(dictName.get(i));
                    }
                }
                minorityDictMap.put(Constants.OTHER_TYPE, newDictName);
            }else{
                minorityDictMap.put(Constants.OTHER_TYPE, Utils.getDictName(Constants.OTHER_DICTIONARY));
            }
        }
    }

    public List<DictTypeBean> getDictTypeData(int userType) {
        switch (userType) {
            case Constants.ACCOUNT_TYPE_DICT_LANGUAGE:
                return dictLanguageData;
        }
        return dictLanguageData;
    }
}
