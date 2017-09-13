package com.onyx.android.dr.data;

import com.onyx.android.dr.DRApplication;
import com.onyx.android.dr.R;
import com.onyx.android.dr.bean.LanguageTypeBean;

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
}
