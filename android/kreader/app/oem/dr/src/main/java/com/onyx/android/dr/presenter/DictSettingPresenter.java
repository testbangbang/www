package com.onyx.android.dr.presenter;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.bean.LanguageTypeBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.DictSettingConfig;
import com.onyx.android.dr.data.DictTypeConfig;
import com.onyx.android.dr.data.database.DictSettingEntity;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.FrenchQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;
import com.onyx.android.dr.interfaces.DictSettingView;
import com.onyx.android.dr.request.local.DictSettingDeleteRequest;
import com.onyx.android.dr.request.local.DictSettingInsert;
import com.onyx.android.dr.util.TimeUtils;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/9.
 */
public class DictSettingPresenter {
    private final DictSettingConfig dictSettingConfig;
    private DictSettingView dictSettingView;

    public DictSettingPresenter(DictSettingView dictSettingView) {
        this.dictSettingView = dictSettingView;
        dictSettingConfig = new DictSettingConfig();
    }

    public void getData() {
        dictSettingView.setDictSettingData(dictSettingConfig.loadData());
    }

    public void saveSettingData(List<LanguageTypeBean> saveLanguageData) {
        DictTypeConfig.dictLanguageData.clear();
        DictTypeBean dictData;
        deleteDictSetting();
        for (int i = 0; i < saveLanguageData.size(); i++) {
            int dictType = saveLanguageData.get(i).getType();
            if (dictType == Constants.ENGLISH_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.english_query), new EnglishQueryEvent(), dictType);
                DictTypeConfig.dictLanguageData.add(dictData);
                insertDictSetting(dictData);
            } else if (dictType == Constants.CHINESE_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.chinese_query_content), new ChineseQueryEvent(), dictType);
                DictTypeConfig.dictLanguageData.add(dictData);
                insertDictSetting(dictData);
            } else if (dictType == Constants.JAPANESE_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.japanese_query_content), new JapaneseQueryEvent(), dictType);
                DictTypeConfig.dictLanguageData.add(dictData);
                insertDictSetting(dictData);
            } else if (dictType == Constants.FRENCH_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.french_query_content), new FrenchQueryEvent(), dictType);
                DictTypeConfig.dictLanguageData.add(dictData);
                insertDictSetting(dictData);
            }
        }
    }

    public void saveSelectDict(int type, ArrayList<Boolean> listCheck, List<DictTypeBean> list) {
        int length = listCheck.size();
        List<DictTypeBean> dictList = new ArrayList<>();
        for (int i = length - 1; i >= 0; i--) {
            if (listCheck.get(i)) {
                //delete basedata data
                DictTypeBean bean = list.get(i);
                dictList.add(bean);
            }
        }
        if (type == Constants.ENGLISH_TAG) {
            DictTypeConfig.englishDictMap.put(type, dictList);
        } else if (type == Constants.CHINESE_TAG) {
            DictTypeConfig.chineseDictMap.put(type, dictList);
        } else if (type == Constants.JAPANESE_TAG) {
            DictTypeConfig.japaneseDictMap.put(type, dictList);
        } else if (type == Constants.FRENCH_TAG) {
            DictTypeConfig.frenchDictMap.put(type, dictList);
        }
    }

    public void insertDictSetting(DictTypeBean dictTypeBean) {
        DictSettingEntity bean = new DictSettingEntity();
        bean.currentTime = TimeUtils.getCurrentTimeMillis();
        bean.tabName = dictTypeBean.getTabName();
        bean.type = dictTypeBean.getType();
        final DictSettingInsert req = new DictSettingInsert(bean);
        dictSettingConfig.insertDictSetting(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void deleteDictSetting() {
        final DictSettingDeleteRequest req = new DictSettingDeleteRequest();
        dictSettingConfig.deleteDictSetting(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }
}
