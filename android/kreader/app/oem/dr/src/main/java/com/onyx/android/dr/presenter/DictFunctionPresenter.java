package com.onyx.android.dr.presenter;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.DictTypeBean;
import com.onyx.android.dr.common.Constants;
import com.onyx.android.dr.data.DictFunctionConfig;
import com.onyx.android.dr.data.DictTypeConfig;
import com.onyx.android.dr.data.QueryRecordData;
import com.onyx.android.dr.data.database.DictSettingEntity;
import com.onyx.android.dr.data.database.QueryRecordEntity;
import com.onyx.android.dr.event.ChineseQueryEvent;
import com.onyx.android.dr.event.EnglishQueryEvent;
import com.onyx.android.dr.event.FrenchQueryEvent;
import com.onyx.android.dr.event.JapaneseQueryEvent;
import com.onyx.android.dr.interfaces.DictResultShowView;
import com.onyx.android.dr.request.local.DictSettingQueryAll;
import com.onyx.android.dr.request.local.QueryRecordInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.common.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/18.
 */
public class DictFunctionPresenter {
    private final DictFunctionConfig functionConfig;
    private final DictTypeConfig dictTypeConfig;
    private final QueryRecordData queryRecordData;
    private DictResultShowView dictView;

    public DictFunctionPresenter(DictResultShowView dictView) {
        this.dictView = dictView;
        functionConfig = new DictFunctionConfig();
        dictTypeConfig = new DictTypeConfig();
        queryRecordData = new QueryRecordData();
    }

    public void loadData(Context context) {
        dictTypeConfig.loadDictInfo(context);
    }

    public void getDictMapData() {
        dictTypeConfig.loadDictMap();
    }

    public void loadDictType(int userType) {
        dictView.setDictTypeData(dictTypeConfig.getDictTypeData(userType));
    }

    public void insertQueryRecord(QueryRecordEntity bean) {
        final QueryRecordInsert req = new QueryRecordInsert(bean);
        queryRecordData.insertQueryRecord(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
            }
        });
    }

    public void getDictSettingData() {
        final DictSettingQueryAll req = new DictSettingQueryAll();
        queryRecordData.getDictSetting(DRApplication.getInstance(), req, new BaseCallback() {
            @Override
            public void done(BaseRequest request, Throwable e) {
                dictView.setDictSettingData(req.getDataList());
            }
        });
    }

    public List<DictTypeBean> getSaveDictSettingData(List<DictSettingEntity> saveLanguageData) {
        List<DictTypeBean> dictLanguageData = new ArrayList<>();
        DictTypeBean dictData;
        for (int i = 0; i < saveLanguageData.size(); i++) {
            int dictType = saveLanguageData.get(i).type;
            if (dictType == Constants.ENGLISH_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.english_query), new EnglishQueryEvent(), dictType);
                dictLanguageData.add(dictData);
            } else if (dictType == Constants.CHINESE_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.chinese_query_content), new ChineseQueryEvent(), dictType);
                dictLanguageData.add(dictData);
            } else if (dictType == Constants.JAPANESE_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.japanese_query_content), new JapaneseQueryEvent(), dictType);
                dictLanguageData.add(dictData);
            } else if (dictType == Constants.FRENCH_TAG) {
                dictData = new DictTypeBean(DRApplication.getInstance().getResources().getString(R.string.french_query_content), new FrenchQueryEvent(), dictType);
                dictLanguageData.add(dictData);
            }
        }
        return dictLanguageData;
    }
}
