package com.onyx.android.dr.request.local;

import com.onyx.android.dr.data.database.DictSettingEntity;
import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.data.request.data.BaseDataRequest;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.List;

/**
 * Created by zhouzhiming on 2017/7/5.
 */
public class DictSettingQueryAll extends BaseDataRequest {
    private List<DictSettingEntity> dataList;

    @Override
    public void execute(DataManager dataManager) throws Exception {
        super.execute(dataManager);
        queryGoodSentenceList();
    }

    public List<DictSettingEntity> getDataList() {
        return dataList;
    }

    public void setGoodSentenceList(List<DictSettingEntity> dataList) {
        this.dataList = dataList;
    }

    public void queryGoodSentenceList() {
        List<DictSettingEntity> goodSentenceList = new Select().from(DictSettingEntity.class).queryList();
        if (goodSentenceList != null && goodSentenceList.size() > 0) {
            setGoodSentenceList(goodSentenceList);
        }
    }
}
