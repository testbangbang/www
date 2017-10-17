package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.FrenchQueryEvent;
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
    public static Map<Integer, List<DictTypeBean>> japaneseDictMap = new ConcurrentHashMap<>();
    public static Map<Integer, List<DictTypeBean>> frenchDictMap = new ConcurrentHashMap<>();

    public void loadDictInfo(Context context) {
        dictLanguageData.clear();
        DictTypeBean dictData = new DictTypeBean(context.getResources().getString(R.string.english_query), new EnglishQueryEvent(), Constants.ENGLISH_TAG);
        dictLanguageData.add(dictData);

        dictData = new DictTypeBean(context.getResources().getString(R.string.chinese_query_content), new ChineseQueryEvent(), Constants.CHINESE_TAG);
        dictLanguageData.add(dictData);

        dictData = new DictTypeBean(context.getResources().getString(R.string.japanese_query_content), new JapaneseQueryEvent(), Constants.JAPANESE_TAG);
        dictLanguageData.add(dictData);

        dictData = new DictTypeBean(context.getResources().getString(R.string.french_query_content), new FrenchQueryEvent(), Constants.FRENCH_TAG);
        dictLanguageData.add(dictData);
    }

    public void loadDictMap() {
        if (whetherLoadData(englishDictMap)){
            List<DictTypeBean> dictName = Utils.getDictName(Constants.ENGLISH_DICTIONARY);
            List<DictTypeBean> newDictName = new ArrayList<>();
            if (dictName != null && dictName.size() > Constants.FOUR) {
                for (int i = 0; i < Constants.FOUR; i++) {
                    if (!newDictName.contains(dictName.get(i))){
                        newDictName.add(dictName.get(i));
                    }
                }
                englishDictMap.put(Constants.ENGLISH_TAG, newDictName);
            }else{
                englishDictMap.put(Constants.ENGLISH_TAG, Utils.getDictName(Constants.ENGLISH_DICTIONARY));
            }
        }
        if (whetherLoadData(chineseDictMap)){
            List<DictTypeBean> dictName = Utils.getDictName(Constants.CHINESE_DICTIONARY);
            List<DictTypeBean> newDictName = new ArrayList<>();
            if (dictName != null && dictName.size() > Constants.FOUR) {
                for (int i = 0; i < Constants.FOUR; i++) {
                    if (!newDictName.contains(dictName.get(i))){
                        newDictName.add(dictName.get(i));
                    }
                }
                chineseDictMap.put(Constants.CHINESE_TAG, newDictName);
            }else{
                chineseDictMap.put(Constants.CHINESE_TAG, Utils.getDictName(Constants.CHINESE_DICTIONARY));
            }
        }
        if (whetherLoadData(japaneseDictMap)){
            List<DictTypeBean> dictName = Utils.getDictName(Constants.JAPANESE_DICTIONARY);
            List<DictTypeBean> newDictName = new ArrayList<>();
            if (dictName != null && dictName.size() > Constants.FOUR) {
                for (int i = 0; i < Constants.FOUR; i++) {
                    if (!newDictName.contains(dictName.get(i))){
                        newDictName.add(dictName.get(i));
                    }
                }
                japaneseDictMap.put(Constants.JAPANESE_TAG, newDictName);
            }else{
                japaneseDictMap.put(Constants.JAPANESE_TAG, Utils.getDictName(Constants.JAPANESE_DICTIONARY));
            }
        }
        if (whetherLoadData(frenchDictMap)){
            List<DictTypeBean> dictName = Utils.getDictName(Constants.FRENCH_DICTIONARY);
            List<DictTypeBean> newDictName = new ArrayList<>();
            if (dictName != null && dictName.size() > Constants.FOUR) {
                for (int i = 0; i < Constants.FOUR; i++) {
                    if (!newDictName.contains(dictName.get(i))){
                        newDictName.add(dictName.get(i));
                    }
                }
                frenchDictMap.put(Constants.FRENCH_TAG, newDictName);
            }else{
                frenchDictMap.put(Constants.FRENCH_TAG, Utils.getDictName(Constants.FRENCH_DICTIONARY));
            }
        }
    }

    public boolean whetherLoadData(Map<Integer, List<DictTypeBean>> map) {
        if (map.size() <= 0) {
            return true;
        }
        for (Map.Entry<Integer, List<DictTypeBean>> entry : map.entrySet()) {
            if (entry.getValue().size() <= 0) {
                return true;
            }
        }
        return false;
    }

    public List<DictTypeBean> getDictTypeData(int userType) {
        switch (userType) {
            case Constants.ACCOUNT_TYPE_DICT_LANGUAGE:
                return dictLanguageData;
        }
        return dictLanguageData;
    }
}
