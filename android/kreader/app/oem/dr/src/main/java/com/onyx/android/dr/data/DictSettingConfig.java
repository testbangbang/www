package com.onyx.android.dr.data;

import android.content.Context;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.LanguageTypeBean;
import com.onyx.android.dr.request.local.DictSettingDeleteRequest;
import com.onyx.android.dr.request.local.DictSettingInsert;
import com.onyx.android.sdk.common.request.BaseCallback;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhiming on 2017/8/9.
 */
public class DictSettingConfig {
    public List<LanguageTypeBean> dataList = new ArrayList<>();
    String[] names = DRApplication.getInstance().getResources().getStringArray(R.array.dict_setting_language);
    int[] numbers = DRApplication.getInstance().getResources().getIntArray(R.array.dict_setting_language_type);

    public List<LanguageTypeBean> loadData() {
        for (int i = 0; i < names.length; i++) {
            LanguageTypeBean bean = new LanguageTypeBean();
            bean.setName(names[i]);
            bean.setType(numbers[i]);
            dataList.add(bean);
        }
        return dataList;
    }

    public void submitRequest(Context context, final BaseDataRequest req, final BaseCallback callBack) {
        DataManager dataManager = DRApplication.getDataManager();
        dataManager.submit(context, req, callBack);
    }

    public void insertDictSetting(Context context, DictSettingInsert req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }

    public void deleteDictSetting(Context context, DictSettingDeleteRequest req, BaseCallback baseCallback) {
        submitRequest(context, req, baseCallback);
    }
}
